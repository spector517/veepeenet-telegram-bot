package com.github.spector517.veepeenet.bot.application.utils;

import com.github.spector517.veepeenet.bot.application.domain.stages.Stage;
import lombok.experimental.UtilityClass;
import org.slf4j.MDC;

@UtilityClass
public class MDCUtils {

    public void setTelegramId(long telegramId) {
        MDC.put("telegramId", String.valueOf(telegramId));
    }

    public void setStage(Stage stage) {
        if (stage == null) {
            return;
        }
        MDC.put("stage", stage.getName());
    }

    public void clearMdc() {
        MDC.clear();
    }
}
