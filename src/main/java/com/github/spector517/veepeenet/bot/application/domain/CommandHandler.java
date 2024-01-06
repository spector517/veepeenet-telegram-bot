package com.github.spector517.veepeenet.bot.application.domain;

import com.github.spector517.veepeenet.bot.application.domain.exceptions.IncorrectCommandArgumentsException;
import com.github.spector517.veepeenet.bot.application.domain.exceptions.UnknownCommandException;
import com.github.spector517.veepeenet.bot.application.domain.stages.Stage;
import com.github.spector517.veepeenet.bot.presentation.commands.Command;
import com.github.spector517.veepeenet.bot.presentation.data.mappers.ClientMapper;
import com.github.spector517.veepeenet.bot.presentation.data.repos.ClientsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Log4j2
@Component
@RequiredArgsConstructor
public class CommandHandler {

    private final ClientsRepo clientsRepo;

    public void handleCommand(Client client) throws IncorrectCommandArgumentsException, UnknownCommandException {
        var commandStr = client.getUpdate().getMessage().getText().trim();
        log.debug("Received command {}", commandStr);
        var splitText = commandStr.split("\\s+");
        var command = Command.fromString(splitText[0]);
        var arguments = splitText.length > 1
                ? Arrays.copyOfRange(splitText, 1, splitText.length)
                : new String[0];
        if (!command.isHasArguments() && arguments.length > 0) {
            throw new IncorrectCommandArgumentsException(command, arguments);
        }
        switch (command) {
            case START -> handleStart(client);
            case STOP -> handleStop(client);
        }
        clientsRepo.save(ClientMapper.toEntity(client));
    }

    private void handleStart(Client client) {
        log.debug("Handle '{}' command", Command.START);
        client.setStage(Stage.START);
        client.setVpnConfigureResult(null);
        client.setSshCheckConnectionResult(null);
    }

    private void handleStop(Client client) {
        log.debug("Handle {} command", Command.STOP);
        client.setStage(Stage.STOP);
    }
}
