package com.github.spector517.veepeenet.bot.application.services;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import com.github.spector517.veepeenet.bot.application.domain.Result;
import com.github.spector517.veepeenet.bot.application.utils.MDCUtils;
import com.github.spector517.veepeenet.bot.presentation.adapters.ssh.SSHConnectionException;
import com.github.spector517.veepeenet.bot.presentation.adapters.ssh.SSHRemoteConnector;
import com.github.spector517.veepeenet.bot.properties.AppProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
@Service
@RequiredArgsConstructor
public class CheckConnectionService implements Runnable {

    private static final String THREAD_NAME = "ssh-conn-checking";

    private final AppProperties appProperties;
    private final Queue<Client> checkSshConnectionQueue = new ConcurrentLinkedQueue<>();

    public void checkSSHConnection(Client client) {
        log.debug("Add client to check SSH-connection processing");
        checkSshConnectionQueue.add(client);
    }

    @Override
    @SneakyThrows
    public void run() {
        while (!Thread.interrupted()) {
            Thread.sleep(appProperties.getSleepingInterval());
            var client = checkSshConnectionQueue.poll();
            if (client == null) {
                continue;
            }
            MDCUtils.setTelegramId(client.getTelegramId());
            MDCUtils.setStage(client.getStage());
            log.info("Start check SSH-connection...");
            var connectionData = new SSHRemoteConnector.ConnectionData(
                    client.getHost(),
                    client.getLogin(),
                    client.getPassword()
            );
            log.debug(connectionData);
            try (var ignored = new SSHRemoteConnector(
                    connectionData,
                    appProperties.getSshConnectionTimeout(),
                    appProperties.getSshExecTimeout()
            )) {
                client.setSshCheckConnectionResult(Result.OK);
                log.info("SSH check success.");
            } catch (SSHConnectionException sshConnectionException) {
                client.setSshCheckConnectionResult(Result.ERROR);
                log.debug(sshConnectionException);
                log.warn("SSH check failure.");
            } finally {
                MDCUtils.clearMdc();
            }
        }
    }

    @PostConstruct
    public void start() {
        log.info("Starting SSH-connection checking service...");
        var thread = new Thread(this);
        thread.setName(THREAD_NAME);
        thread.start();
        log.debug("Sleeping interval: {}", appProperties.getSleepingInterval());
        log.info("SSH-connection checking service started.");
    }
}
