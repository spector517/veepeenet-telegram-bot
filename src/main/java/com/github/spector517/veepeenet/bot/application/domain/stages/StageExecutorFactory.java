package com.github.spector517.veepeenet.bot.application.domain.stages;

import com.github.spector517.veepeenet.bot.application.domain.stages.executors.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;

@Component
public class StageExecutorFactory {

    private final EnumMap<Stage, StageExecutor> stageExecutorMap;

    public StageExecutorFactory(
            DefaultExecutor defaultExecutor,
            CheckConnExecutor checkConnExecutor,
            ClientsCorrectExecutor clientsCorrectExecutor,
            ClientsReqExecutor clientsReqExecutor,
            ConfirmConnExecutor confirmConnExecutor,
            HostReqExecutor hostReqExecutor,
            LoginReqExecutor loginReqExecutor,
            PassReqExecutor passReqExecutor,
            RunDeployExecutor runDeployExecutor,
            StopExecutor stopExecutor
    ) {
        stageExecutorMap = new EnumMap<>(Stage.class);
        for (Stage stage: Stage.values()) {
            var executor = switch (stage) {
                case START, VPN_ADVANTAGES, ABOUT_PROCEDURE, VPS_RENT, CONN_SUCCESS, CONN_ERROR,
                        CLIENTS_RULES, CLIENTS_INCORRECT, DEPLOY_SUCCESS, DEPLOY_FAIL -> defaultExecutor;
                case CHECK_CONN -> checkConnExecutor;
                case CLIENTS_CORRECT -> clientsCorrectExecutor;
                case CLIENTS_REQ -> clientsReqExecutor;
                case CONFIRM_CONN -> confirmConnExecutor;
                case HOST_REQ -> hostReqExecutor;
                case LOGIN_REQ -> loginReqExecutor;
                case PASS_REQ -> passReqExecutor;
                case RUN_DEPLOY -> runDeployExecutor;
                case STOP -> stopExecutor;
            };
            stageExecutorMap.put(stage, executor);
        }
    }

    public StageExecutor get(Stage stage) {
        return stageExecutorMap.get(stage);
    }
}
