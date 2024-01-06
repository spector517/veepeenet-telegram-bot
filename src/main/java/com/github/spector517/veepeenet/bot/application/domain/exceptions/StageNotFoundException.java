package com.github.spector517.veepeenet.bot.application.domain.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StageNotFoundException extends Exception {

    private final String stageName;

    @Override
    public String toString() {
        return "Stage with name '%s' not found".formatted(stageName);
    }
}
