package com.github.spector517.veepeenet.bot.application.domain.stages;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.spector517.veepeenet.bot.application.domain.exceptions.StageNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.HashMap;

@Getter
@RequiredArgsConstructor
public enum Stage {

    START("start"),
    RESTART("restart"),
    VPN_ADVANTAGES("vpn-advantages"),
    ABOUT_PROCEDURE("about-procedure"),
    VPS_RENT("vps-rent"),
    HOST_VALIDATION_ERROR("host-validation-error"),
    LOGIN_VALIDATION_ERROR("login-validation-error"),
    CONN_SUCCESS("conn-success"),
    CONN_ERROR("conn-error"),
    CLIENTS_RULES("clients-rules"),
    CLIENTS_INCORRECT("clients-incorrect"),
    DEPLOY_SUCCESS("deploy-success"),
    DEPLOY_FAIL("deploy-fail"),

    HOST_REQ("host-req"),
    LOGIN_REQ("login-req"),
    STOP("stop"),
    PASS_REQ("pass-req"),
    CONFIRM_CONN("confirm-conn"),
    CHECK_CONN("check-conn"),
    CLIENTS_REQ("clients-req"),
    CLIENTS_CORRECT("clients-correct"),
    RUN_DEPLOY("run-deploy");

    @JsonValue
    private final String name;
    private final StageData data;

    @SneakyThrows
    Stage(String name) {
        this.name = name;
        var stageDataMap = new ObjectMapper(new YAMLFactory()).readValue(
                getClass().getClassLoader().getResourceAsStream("stages.yaml"),
                new TypeReference<HashMap<String, StageData>>() {}
        );
        if (stageDataMap.containsKey(name)) {
            data = stageDataMap.get(name);
        } else {
            throw new StageNotFoundException(name);
        }
    }

    public static Stage fromString(String name) throws StageNotFoundException {
        for (var stage: Stage.values()) {
            if (stage.getName().equals(name)) {
                return stage;
            }
        }
        throw new StageNotFoundException(name);
    }
}
