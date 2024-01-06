package com.github.spector517.veepeenet.bot.application.domain.stages.executors;

import com.github.spector517.veepeenet.bot.application.domain.Bot;
import com.github.spector517.veepeenet.bot.application.domain.Client;
import com.github.spector517.veepeenet.bot.application.domain.stages.StageData;
import com.github.spector517.veepeenet.bot.application.utils.RenderUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultExecutor implements StageExecutor {

    @Override
    public boolean init(Client client) throws Exception {
        var stageData = client.getStage().getData();
        final String text;
        if (stageData.isTemplate()) {
            text = RenderUtils.render(stageData.getText(), client);
        } else {
            text = stageData.getText();
        }
        var message = SendMessage.builder()
                .text(text)
                .parseMode(stageData.getParseMode().getType())
                .chatId(client.getTelegramId())
                .build();
        if (stageData.getChooses() != null) {
            var inlineKeyboardMarkup = new InlineKeyboardMarkup();
            var buttons = stageData.getChooses().stream().map(choose ->
                    InlineKeyboardButton.builder()
                            .text(choose.getDisplay())
                            .callbackData(choose.getTo())
                            .build()
            ).map(List::of).toList();
            inlineKeyboardMarkup.setKeyboard(buttons);
            message.setReplyMarkup(inlineKeyboardMarkup);
        }
        client.getBot().execute(message);
        return true;
    }

    @Override
    public boolean complete(Client client) throws Exception {
        var update = client.getUpdate();
        if (update.hasCallbackQuery()) {
            for (StageData.Choice choice: client.getStage().getData().getChooses()) {
                if (update.getCallbackQuery().getData().equals(choice.getTo())) {
                    removeReplyKeyboard(update.getCallbackQuery(), client.getBot());
                    return true;
                }
            }
        }
        return false;
    }

    protected void removeReplyKeyboard(CallbackQuery callbackQuery, Bot bot) throws Exception {
        removeReplyKeyboard(
                callbackQuery.getFrom().getId(),
                callbackQuery.getMessage().getMessageId(),
                bot
        );
    }

    private void removeReplyKeyboard(long chatId, int messageId, Bot bot) throws TelegramApiException {
        bot.execute(EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build()
        );
    }
}
