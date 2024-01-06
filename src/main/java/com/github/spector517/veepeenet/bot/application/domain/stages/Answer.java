package com.github.spector517.veepeenet.bot.application.domain.stages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.spector517.veepeenet.bot.application.domain.exceptions.UnsupportedClientAnswerException;
import org.telegram.telegrambots.meta.api.objects.Update;

public enum Answer {

    @JsonProperty("callback") CALLBACK,
    @JsonProperty("message") MESSAGE;

    public static Answer fromUpdate(Update update) throws UnsupportedClientAnswerException {
        if (update.hasMessage()) {
            return MESSAGE;
        } else if (update.hasCallbackQuery()) {
            return CALLBACK;
        } else {
            throw new UnsupportedClientAnswerException(update);
        }
    }
}
