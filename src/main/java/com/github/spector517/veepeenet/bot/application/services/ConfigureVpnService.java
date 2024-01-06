package com.github.spector517.veepeenet.bot.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.spector517.veepeenet.bot.application.domain.Client;
import com.github.spector517.veepeenet.bot.application.domain.Result;
import com.github.spector517.veepeenet.bot.application.utils.MDCUtils;
import com.github.spector517.veepeenet.bot.presentation.adapters.ssh.SSHConnectionException;
import com.github.spector517.veepeenet.bot.presentation.adapters.ssh.SSHRemoteConnector;
import com.github.spector517.veepeenet.bot.presentation.adapters.ssh.dto.ScriptResult;
import com.github.spector517.veepeenet.bot.properties.AppProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ConfigureVpnService implements Runnable {

    private static final String THREAD_NAME = "vpn-configuring";
    private static final String HOME_DIR_ALIAS = "~";
    private static final String VPN_CLIENTS_DELIMITER = " ";
    private static final String VPN_RESULT_FILE = ".veepeenet/wg/result.json";
    private static final String VPN_CLIENT_CONFIG_EXT = "conf";

    private final AppProperties appProperties;
    private final Queue<Client> configureVpnQueue = new ConcurrentLinkedQueue<>();
    private final InputStream deployVpnScriptInputStream;

    public void configureVPN(Client client) {
        log.debug("Add client to configure VPN processing");
        configureVpnQueue.add(client);
    }

    @Override
    @SneakyThrows
    public void run() {
        while (!Thread.interrupted()) {
            Thread.sleep(appProperties.getSleepingInterval());
            var client = configureVpnQueue.poll();
            if (client == null) {
                continue;
            }
            MDCUtils.setTelegramId(client.getTelegramId());
            MDCUtils.setStage(client.getStage());
            log.info("Start configuring VPN...");
            var connectionData = new SSHRemoteConnector.ConnectionData(
                    client.getHost(),
                    client.getLogin(),
                    client.getPassword()
            );
            try (var sshConnector = new SSHRemoteConnector(
                    connectionData,
                    appProperties.getSshExecTimeout(),
                    appProperties.getSshExecTimeout()
            )) {
                sshConnector.putFile(appProperties.getVpnScriptRemotePath(), deployVpnScriptInputStream);
                var command = "%s %s --clean --host %s --output %s --add-clients %s".formatted(
                        appProperties.getRemotePythonInterpreter(),
                        appProperties.getVpnScriptRemotePath(),
                        client.getHost(),
                        Path.of(HOME_DIR_ALIAS, appProperties.getVpnClientsRemoteDir()),
                        String.join(VPN_CLIENTS_DELIMITER, client.getClientsNames())
                );
                var runScriptCode = sshConnector.runCommand(command);
                if (runScriptCode != 0) {
                    log.warn("VPN configuring command return non-zero exit code '{}'.", runScriptCode);
                    client.setVpnConfigureResult(Result.ERROR);
                    return;
                }
                var resultBytes = sshConnector.fetchFile(VPN_RESULT_FILE);
                var scriptResult = new ObjectMapper().readValue(resultBytes, ScriptResult.class);
                if (scriptResult.isHasError()) {
                    log.warn("VPN configuring result has errors.");
                    client.setVpnConfigureResult(Result.ERROR);
                    return;
                }
                client.setClientsVpnConfigs(client.getClientsNames().stream()
                        .map(clientName -> "%s.%s".formatted(clientName, VPN_CLIENT_CONFIG_EXT))
                        .collect(Collectors.toMap(
                                fileName -> fileName,
                                fileName ->
                                        sshConnector.fetchFile(Path.of(appProperties.getVpnClientsRemoteDir(), fileName)
                                                .toString())
                        ))
                );
                client.setVpnConfigureResult(Result.OK);
                log.info("VPN configured successfully.");
            } catch (SSHConnectionException sshConnectionException) {
                log.debug(sshConnectionException);
                log.warn("Unknown error while configuring VPN.");
                client.setVpnConfigureResult(Result.ERROR);
            } finally {
                MDCUtils.clearMdc();
            }
        }
    }

    @PostConstruct
    public void start() {
        log.info("Starting configure VPN service...");
        var thread = new Thread(this);
        thread.setName(THREAD_NAME);
        thread.start();
        log.debug("Sleeping interval: {}", appProperties.getSleepingInterval());
        log.info("Configure VPN service started.");
    }
}
