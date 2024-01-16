package com.github.spector517.veepeenet.bot.application.domain.stages.executors;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import com.github.spector517.veepeenet.bot.application.domain.exceptions.StageExecutionException;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Component;

@Component
public class HostReqExecutor extends DefaultExecutor {

    public HostReqExecutor() {
        super();
    }

    @Override
    public boolean init(Client client) throws Exception {
        client.setSshCheckConnectionResult(null);
        return super.init(client);
    }

    @Override
    public boolean complete(Client client) throws Exception {
        var update = client.getUpdate();
        if (update.hasMessage()) {
            var ip = update.getMessage().getText().trim();
            if (!isValidIp(ip)) {
                throw new StageExecutionException("IP-address '%s' is invalid".formatted(ip));
            }
            client.setHost(ip);
            return true;
        }
        throw new IllegalStateException("Text message expected, but not found.");
    }

    private boolean isValidIp(String ip) {
        return InetAddressValidator.getInstance().isValid(ip);
    }
}
