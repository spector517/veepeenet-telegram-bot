package com.github.spector517.veepeenet.bot.application.domain.exceptions;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Getter
public class UnexpectedCallbackQueryException extends Exception {

    private final CallbackQuery callbackQuery;

    public UnexpectedCallbackQueryException(CallbackQuery callbackQuery) {
        this.callbackQuery = callbackQuery;
    }

    @Override
    public String toString() {
        return "Unexpected callback query with data '%s'.".formatted(callbackQuery.getData());
    }
}
