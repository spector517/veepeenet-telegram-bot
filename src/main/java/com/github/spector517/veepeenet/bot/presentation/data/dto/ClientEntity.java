package com.github.spector517.veepeenet.bot.presentation.data.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientEntity {

    private long telegramId;
    private String name;
    private String stageName;
    private boolean stageInitiated;
    private boolean stageCompleted;
    private String host;
    private String login;
    private String password;
    private List<String> clientNames;
    private String sshConnectionStatus;
    private String vpnConfigureResult;
}
