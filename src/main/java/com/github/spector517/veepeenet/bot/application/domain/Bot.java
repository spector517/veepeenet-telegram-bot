package com.github.spector517.veepeenet.bot.application.domain;

import com.github.spector517.veepeenet.bot.application.domain.stages.Answer;
import com.github.spector517.veepeenet.bot.application.services.ClientStagesProcessingService;
import com.github.spector517.veepeenet.bot.application.utils.MDCUtils;
import com.github.spector517.veepeenet.bot.presentation.data.mappers.ClientMapper;
import com.github.spector517.veepeenet.bot.presentation.data.repos.ClientsRepo;
import com.github.spector517.veepeenet.bot.properties.AppProperties;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j2
@Getter
@Component
public class Bot extends TelegramLongPollingBot {

    private final AppProperties appProperties;
    private final TelegramBotsApi telegramBotsApi;
    private final ClientStagesProcessingService clientStagesProcessingService;
    private final ClientsRepo clientsRepo;
    private final CommandHandler commandHandler;

    public Bot(
            AppProperties appProperties,
            TelegramBotsApi telegramBotsApi,
            ClientStagesProcessingService clientStagesProcessingService,
            ClientsRepo clientsRepo,
            CommandHandler commandHandler
    ) {
        super(appProperties.getToken());
        this.appProperties = appProperties;
        this.telegramBotsApi = telegramBotsApi;
        this.clientStagesProcessingService = clientStagesProcessingService;
        this.clientsRepo = clientsRepo;
        this.commandHandler = commandHandler;
    }

    @Override
    public String getBotUsername() {
        return appProperties.getName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            var client = getClient(update);
            var answer = Answer.fromUpdate(update);
            if (answer.equals(Answer.MESSAGE) && update.getMessage().getText().startsWith("/")) {
                commandHandler.handleCommand(client);
                clientStagesProcessingService.processClient(client);
                return;
            }
            if (client.getStage() == null) {
                log.warn("Client has no stage. Only commands allowed.");
                return;
            }
            if (!answer.equals(client.getStage().getData().getAllowedAnswer())) {
                log.warn("Answer '{}' are not allowed for current stage", answer);
                return;
            }
            clientStagesProcessingService.processClient(client);
            MDCUtils.clearMdc();
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    private Client getClient(Update update) {
        var telegramId = getTelegramId(update);
        MDCUtils.setTelegramId(telegramId);
        var clientEntityOptional = clientsRepo.find(telegramId);
        if (clientEntityOptional.isPresent()) {
            var client = ClientMapper.toClient(clientEntityOptional.get(), this);
            client.setUpdate(update);
            return client;
        } else {
            log.info("Client to found in repo. Creating...");
            var client = Client.builder()
                    .telegramId(telegramId)
                    .name(getClientName(update))
                    .bot(this)
                    .update(update)
                    .build();
            log.info("Client created.");
            clientsRepo.save(ClientMapper.toEntity(client));
            return client;
        }
    }

    @SneakyThrows
    private long getTelegramId(Update update) {
        var answer = Answer.fromUpdate(update);
        if (Answer.MESSAGE.equals(answer)) {
            return update.getMessage().getFrom().getId();
        } else {
            return update.getCallbackQuery().getFrom().getId();
        }
    }

    private String getClientName(Update update) {
        var user = update.hasMessage()
                ? update.getMessage().getFrom()
                : update.getCallbackQuery().getFrom();
        return user.getFirstName();
    }

    @PostConstruct
    @SneakyThrows
    public void connect() {
        log.info("Connecting bot...");
        telegramBotsApi.registerBot(this);
        log.info("Bot connected.");
    }
}
