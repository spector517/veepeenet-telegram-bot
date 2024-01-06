package com.github.spector517.veepeenet.bot.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Result {
    OK("ok"),
    ERROR("error");

    private final String type;

    public static Result fromString(String sshConnStatus) {
        return switch (sshConnStatus) {
            case "ok" -> OK;
            case "error" -> ERROR;
            default -> null;
        };
    }
}
