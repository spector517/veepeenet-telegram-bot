package com.github.spector517.veepeenet.bot.application.services;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import com.github.spector517.veepeenet.bot.application.domain.stages.StageExecutorFactory;
import com.github.spector517.veepeenet.bot.application.utils.MDCUtils;
import com.github.spector517.veepeenet.bot.presentation.data.mappers.ClientMapper;
import com.github.spector517.veepeenet.bot.presentation.data.repos.ClientsRepo;
import com.github.spector517.veepeenet.bot.properties.AppProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
@Log4j2
public class ClientStagesProcessingService implements Runnable {

    private final ClientsRepo clientsRepo;
    private final Queue<Client> processingClientsQueue = new ConcurrentLinkedQueue<>();
    private final AppProperties appProperties;
    private final StageExecutorFactory stageExecutorFactory;

    public void processClient(Client client) {
        if (client.getStage() == null) {
            log.debug("Stage is null, skip client");
            return;
        }
        processingClientsQueue.add(client);
        log.debug("Client added to processing");
    }

    @Override
    @SneakyThrows
    public void run() {
        while(!Thread.interrupted()) {
            Thread.sleep(appProperties.getSleepingInterval());
            var client = processingClientsQueue.poll();
            if (client == null) {
                continue;
            }
            MDCUtils.setTelegramId(client.getTelegramId());
            MDCUtils.setStage(client.getStage());
            if (!client.isCurrentStageInitiated()) {
                processInitiation(client);
                continue;
            }
            if (!client.isCurrentStageCompleted()) {
                processCompletion(client);
            }
            MDCUtils.clearMdc();
        }
    }

    private void processInitiation(Client client) {
        try {
            client.setCurrentStageInitiated(stageExecutorFactory.get(client.getStage()).init(client));
            log.info("Stage initiated");
        } catch (Exception ex) {
            log.warn("Initializing of stage failed");
            log.debug(ex);
            client.setFailStage();
            MDCUtils.setStage(client.getStage());
            log.info("Stage changed to fail stage");
        }
        if (!client.isCurrentStageInitiated() || client.getStage().getData().isThrough()) {
            processClient(client);
        }
        clientsRepo.save(ClientMapper.toEntity(client));
    }

    private void processCompletion(Client client) {
        try {
            client.setCurrentStageCompleted(stageExecutorFactory.get(client.getStage()).complete(client));
        } catch (Exception ex) {
            log.warn("Completion of stage failed");
            log.debug(ex);
            client.setFailStage();
            MDCUtils.setStage(client.getStage());
            log.info("Stage changed to fail stage");
        }
        if (client.isCurrentStageCompleted()) {
            log.info("Stage completed");
            client.setNextStage();
            MDCUtils.setStage(client.getStage());
            log.info(client.getStage() != null ? "Stage changed to next stage" : "Final stage");
        }
        clientsRepo.save(ClientMapper.toEntity(client));
        processClient(client);
    }

    @PostConstruct
    public void start() {
        log.info("Starting client stages service...");
        var clientProcessingThread = new Thread(this);
        clientProcessingThread.setName("client-stages-processing");
        clientProcessingThread.start();
        log.debug("Sleeping interval: %d".formatted(appProperties.getSleepingInterval()));
        log.info("Client stages service started.");
    }
}
