package com.github.spector517.veepeenet.bot.application.domain.stages.executors;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import com.github.spector517.veepeenet.bot.application.domain.exceptions.StageExecutionException;
import com.github.spector517.veepeenet.bot.application.services.CheckConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckConnExecutor extends DefaultExecutor {

    private final CheckConnectionService checkConnectionService;

    @Override
    public boolean init(Client client) throws Exception {
        checkConnectionService.checkSSHConnection(client);
        return super.init(client);
    }

    @Override
    public boolean complete(Client client) throws Exception {
        return switch (client.getSshCheckConnectionResult()) {
            case null -> false;
            case OK -> true;
            case ERROR -> throw new StageExecutionException("SSH connection checking failure");
        };
    }
}
