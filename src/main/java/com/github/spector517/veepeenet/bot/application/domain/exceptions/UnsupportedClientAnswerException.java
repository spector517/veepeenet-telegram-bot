package com.github.spector517.veepeenet.bot.application.domain.exceptions;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public class UnsupportedClientAnswerException extends Exception {

    private final Update update;

    @Override
    public String toString() {
        return """
                Unsupported client answer in update:
                
                hasEditedMessage: %s,
                hasInlineQuery: %s,
                hasPoll: %s,
                hasPollAnswer: %s,
                hasPreCheckoutQuery: %s,
                hasShippingQuery: %s
                """
                .formatted(
                        update.hasEditedMessage(),
                        update.hasInlineQuery(),
                        update.hasPoll(),
                        update.hasPollAnswer(),
                        update.hasPreCheckoutQuery(),
                        update.hasShippingQuery()
                );
    }
}
