package com.github.spector517.veepeenet.bot.application.domain.stages.executors;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import com.github.spector517.veepeenet.bot.application.domain.exceptions.StageExecutionException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ClientsReqExecutor extends DefaultExecutor {

    private final static String PATTERN = "^[A-Za-z][A-z0-9]{0,15}$";

    public ClientsReqExecutor() {
        super();
    }

    @Override
    public boolean complete(Client client) throws Exception {
        var receivedClients = client.getUpdate().getMessage().getText().trim().split("\\s");
        if (receivedClients.length > 10) {
            throw new StageExecutionException("Too many clients.");
        }
        for (var receivedClient: receivedClients) {
            if (!receivedClient.matches(PATTERN)) {
                throw new StageExecutionException("Error client naming '%s'".formatted(receivedClient));
            }
        }
        client.setClientsNames(Arrays.asList(receivedClients));
        return true;
    }
}
