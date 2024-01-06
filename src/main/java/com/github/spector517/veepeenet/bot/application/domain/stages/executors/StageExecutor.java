package com.github.spector517.veepeenet.bot.application.domain.stages.executors;

import com.github.spector517.veepeenet.bot.application.domain.Client;

public interface StageExecutor {

    boolean init(Client client) throws Exception;

    boolean complete(Client client) throws Exception;
}