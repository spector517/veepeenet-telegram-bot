package com.github.spector517.veepeenet.bot.application.domain.stages.executors;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

@Component
public class ConfirmConnExecutor extends DefaultExecutor {

    public ConfirmConnExecutor() {
        super();
    }

    @Override
    public boolean init(Client client) throws Exception {
        var update = client.getUpdate();
        if (update.hasMessage()) {
            var deleteMessage = DeleteMessage.builder()
                    .chatId(update.getMessage().getFrom().getId())
                    .messageId(update.getMessage().getMessageId())
                    .build();
            client.getBot().execute(deleteMessage);
            return super.init(client);
        }
        return false;
    }
}
