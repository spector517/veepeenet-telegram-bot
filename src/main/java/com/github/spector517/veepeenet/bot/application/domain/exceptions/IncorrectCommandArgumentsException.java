package com.github.spector517.veepeenet.bot.application.domain.exceptions;

import com.github.spector517.veepeenet.bot.presentation.commands.Command;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IncorrectCommandArgumentsException extends Exception {

    private final Command command;
    private final String[] arguments;

    @Override
    public String toString() {
        return "Command %s%s expect arguments, but received %s arguments.".formatted(
                command,
                command.isHasArguments() ? "" : " does not",
                arguments.length > 0 ? arguments.length : "no"
        );
    }
}
