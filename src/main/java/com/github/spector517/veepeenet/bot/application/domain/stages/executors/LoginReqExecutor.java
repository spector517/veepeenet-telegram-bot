package com.github.spector517.veepeenet.bot.application.domain.stages.executors;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import com.github.spector517.veepeenet.bot.application.domain.exceptions.StageExecutionException;
import org.springframework.stereotype.Component;

@Component
public class LoginReqExecutor extends DefaultExecutor {

    private static final String LINUX_USER_NAME_REGEX = "^[a-z_]([a-z0-9_-]{0,31}|[a-z0-9_-]{0,30}\\$)$";

    public LoginReqExecutor() {
        super();
    }

    @Override
    public boolean complete(Client client) throws Exception {
        var update = client.getUpdate();
        if (update.hasMessage()) {
            var username = update.getMessage().getText().trim();
            if (!isValidUsername(username)) {
                throw new StageExecutionException("Host username '%s' is invalid".formatted(username));
            }
            client.setLogin(username);
            return true;
        }
        throw new IllegalStateException("Text message expected, but not found.");
    }

    private boolean isValidUsername(String username) {
        return username.matches(LINUX_USER_NAME_REGEX);
    }
}
