package com.github.spector517.veepeenet.bot.application.domain.stages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StageData {

    private String text;
    @JsonProperty(value = "parse_mode")
    private ParseMode parseMode = ParseMode.PLAIN;
    @JsonProperty(value = "allowed_answer")
    private Answer allowedAnswer;
    private boolean template;
    private String next;
    private String fail;
    private boolean through;
    private List<Choice> chooses;

    @Data
    public static class Choice {
        private String display;
        private String to;
    }
}
