package com.github.spector517.veepeenet.bot.application.domain.stages.executors;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import org.springframework.stereotype.Component;

@Component
public class HostReqExecutor extends DefaultExecutor {

    public HostReqExecutor() {
        super();
    }

    @Override
    public boolean complete(Client client) {
        var update = client.getUpdate();
        if (update.hasMessage()) {
            client.setHost(update.getMessage().getText().trim());
            return true;
        }
        return false;
    }
}
