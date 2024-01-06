package com.github.spector517.veepeenet.bot.application.domain.stages.executors;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientsCorrectExecutor extends DefaultExecutor {

    public ClientsCorrectExecutor() {
        super();
    }

    @Override
    public boolean init(Client client) throws Exception {
        return super.init(client);
    }
}
