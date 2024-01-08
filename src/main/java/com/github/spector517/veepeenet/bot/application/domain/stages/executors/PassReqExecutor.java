package com.github.spector517.veepeenet.bot.application.domain.stages.executors;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import org.springframework.stereotype.Component;

@Component
public class PassReqExecutor extends DefaultExecutor {

    public PassReqExecutor() {
        super();
    }

    @Override
    public boolean complete(Client client) {
        var update = client.getUpdate();
        if (update.hasMessage()) {
            client.setPassword(update.getMessage().getText().trim());
            return true;
        }
        throw new IllegalStateException("Text message expected, but not found.");
    }
}
