package com.github.spector517.veepeenet.bot.application.domain.stages.executors;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import com.github.spector517.veepeenet.bot.application.domain.exceptions.StageExecutionException;
import com.github.spector517.veepeenet.bot.application.services.ConfigureVpnService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;

import java.io.ByteArrayInputStream;

@Component
public class RunDeployExecutor extends DefaultExecutor {

    private final ConfigureVpnService configureVpnService;

    public RunDeployExecutor(ConfigureVpnService configureVpnService) {
        super();
        this.configureVpnService = configureVpnService;
    }

    @Override
    public boolean init(Client client) throws Exception {
        configureVpnService.configureVPN(client);
        return super.init(client);
    }

    @Override
    public boolean complete(Client client) throws Exception {
        return switch (client.getVpnConfigureResult()) {
            case null -> false;
            case OK -> {
                if (client.getClientsVpnConfigs().size() == 1) {
                    client.getBot().execute(getDocument(client));
                } else {
                    client.getBot().execute(getMediaGroup(client));
                }
                yield true;
            }
            case ERROR -> throw new StageExecutionException("VPN configuring failure");
        };
    }

    private SendMediaGroup getMediaGroup(Client client) {
        return SendMediaGroup.builder()
                .medias(
                        client.getClientsVpnConfigs().entrySet().stream().map(configEntry ->
                                InputMediaDocument.builder()
                                        .newMediaStream(new ByteArrayInputStream(configEntry.getValue()))
                                        .mediaName(configEntry.getKey())
                                        .isNewMedia(true)
                                        .media("attach://%s".formatted(configEntry.getKey()))
                                        .build()
                        ).map(InputMedia.class::cast).toList()
                )
                .chatId(client.getTelegramId())
                .build();

    }

    private SendDocument getDocument(Client client) {
        var clientConfig = client.getClientsVpnConfigs().entrySet().stream().findAny().get();
        return SendDocument.builder()
                .document(new InputFile(new ByteArrayInputStream(clientConfig.getValue()), clientConfig.getKey()))
                .chatId(client.getTelegramId())
                .build();
    }
}
