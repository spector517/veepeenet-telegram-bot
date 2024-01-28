package com.github.spector517.veepeenet.bot.presentation.data.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@Entity
@Table(name = "bot_clients")
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity {

    @Id
    private long id;
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
