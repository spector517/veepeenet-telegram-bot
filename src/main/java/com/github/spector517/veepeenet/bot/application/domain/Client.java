package com.github.spector517.veepeenet.bot.application.domain;

import com.github.spector517.veepeenet.bot.application.domain.exceptions.StageNotFoundException;
import com.github.spector517.veepeenet.bot.application.domain.stages.Stage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class Client {

    private final long telegramId;
    private final String name;
    private final Bot bot;

    private Stage stage;
    private boolean currentStageInitiated;
    private boolean currentStageCompleted;

    @Builder.Default
    private String host = "";
    @Builder.Default
    private String login = "";
    @Builder.Default
    private String password = "";
    @Builder.Default
    private List<String> clientsNames = new ArrayList<>();
    private Result sshCheckConnectionResult;
    private Result vpnConfigureResult;

    private Map<String, byte[]> clientsVpnConfigs;
    private Update update;

    public void setStage(Stage stage) {
        currentStageInitiated = false;
        currentStageCompleted = false;
        this.stage = stage;
    }

    public void setNextStage() {
        try {
            if (update.hasCallbackQuery() && !stage.getData().isThrough()) {
                setStage(Stage.fromString(update.getCallbackQuery().getData()));
            } else {
                setStage(Stage.fromString(stage.getData().getNext()));
            }
        } catch (StageNotFoundException stageNotFoundException) {
            setStage(null);
        }
    }

    public void setFailStage() {
        try {
            setStage(Stage.fromString(stage.getData().getFail()));
        } catch (StageNotFoundException stageNotFoundException) {
            setStage(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Client client) {
            return client.getTelegramId() == telegramId;
        }
        return false;
    }
}
