package com.github.spector517.veepeenet.bot.application.utils;

import com.github.spector517.veepeenet.bot.application.domain.Client;
import com.github.spector517.veepeenet.bot.application.domain.stages.ParseMode;
import com.hubspot.jinjava.Jinjava;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class RenderUtils {

    private static final Jinjava TEMPLATE_ENGINE = new Jinjava();

    public String render(String template, Client client) {
        var parseMode = client.getStage().getData().getParseMode();
        var clientNames = client.getClientsNames().stream().map(clientName ->
                getSafeValue(clientName, parseMode)
        ).toList();
        var context = Map.of(
                "name", getSafeValue(client.getName(), parseMode),
                "host", getSafeValue(client.getHost(), parseMode),
                "login", getSafeValue(client.getLogin(), parseMode),
                "password", getSafeValue(client.getPassword(), parseMode),
                "clients", clientNames
        );
        return TEMPLATE_ENGINE.render(template, context);
    }

    private String getSafeValue(String unsafeText, ParseMode parseMode) {
        return ParseMode.MARKDOWN_V2.equals(parseMode)
                ? MessageUtils.escapeMD2Chars(unsafeText)
                : unsafeText;
    }
}
