package com.github.spector517.veepeenet.bot.presentation.adapters.ssh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ScriptResult {

    @JsonProperty("has_error")
    private boolean hasError;
    private Meta meta;
    private List<Action> actions;

    @Data
    public static class Action {

        private String result;
        private String name;
        private String args;
        private String kwargs;
        private String error;
    }

    @Data
    public static class Meta {

        private String interpreter;
        @JsonProperty("interpreter_version")
        private String interpreterVersion;
        @JsonProperty("run_args")
        private List<String> runArgs;
    }
}
