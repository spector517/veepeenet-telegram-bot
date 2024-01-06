package com.github.spector517.veepeenet.bot.presentation.commands;

import com.github.spector517.veepeenet.bot.application.domain.exceptions.UnknownCommandException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Command {

    START(false),
    STOP(false);

    private final boolean hasArguments;

    public static Command fromString(String strCommand) throws UnknownCommandException {
        return switch (strCommand) {
            case "/start" -> START;
            case "/stop" -> STOP;
            default -> throw new UnknownCommandException("Unknown command %s".formatted(strCommand));
        };
    }
}
