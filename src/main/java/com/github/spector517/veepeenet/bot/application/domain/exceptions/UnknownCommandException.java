package com.github.spector517.veepeenet.bot.application.domain.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnknownCommandException extends Exception {

    private final String strCommand;

    @Override
    public String toString() {
        return "Unknown command '%s'".formatted(strCommand);
    }
}
