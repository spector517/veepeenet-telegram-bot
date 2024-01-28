package com.github.spector517.veepeenet.bot.presentation.data.mappers;

import com.github.spector517.veepeenet.bot.application.domain.Bot;
import com.github.spector517.veepeenet.bot.application.domain.Client;
import com.github.spector517.veepeenet.bot.application.domain.Result;
import com.github.spector517.veepeenet.bot.application.domain.exceptions.StageNotFoundException;
import com.github.spector517.veepeenet.bot.application.domain.stages.Stage;
import com.github.spector517.veepeenet.bot.presentation.data.dto.ClientEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClientMapper {

    public ClientEntity toEntity(Client client) {
        return ClientEntity.builder()
                .id(client.getTelegramId())
                .name(client.getName())
                .stageName(client.getStage() != null ? client.getStage().getName() : null)
                .stageInitiated(client.isCurrentStageInitiated())
                .stageCompleted(client.isCurrentStageCompleted())
                .host(client.getHost())
                .login(client.getLogin())
                .password(client.getPassword())
                .clientNames(client.getClientsNames())
                .sshConnectionStatus(client.getSshCheckConnectionResult() != null
                        ? client.getSshCheckConnectionResult().getType()
                        : null
                )
                .vpnConfigureResult(client.getVpnConfigureResult() != null
                        ? client.getVpnConfigureResult().getType()
                        :null
                )
                .build();
    }

    public Client toClient(ClientEntity clientEntity, Bot bot) {
        Stage stage;
        try {
            stage = clientEntity.getStageName() != null ? Stage.fromString(clientEntity.getStageName()) : null;
        } catch (StageNotFoundException stageNotFoundException) {
            stage = null;
        }
        return Client.builder()
                .telegramId(clientEntity.getId())
                .name(clientEntity.getName())
                .bot(bot)
                .stage(stage)
                .currentStageInitiated(clientEntity.isStageInitiated())
                .currentStageCompleted(clientEntity.isStageCompleted())
                .host(clientEntity.getHost())
                .login(clientEntity.getLogin())
                .password(clientEntity.getPassword())
                .clientsNames(clientEntity.getClientNames())
                .sshCheckConnectionResult(clientEntity.getSshConnectionStatus() != null
                        ? Result.fromString(clientEntity.getSshConnectionStatus())
                        : null
                )
                .vpnConfigureResult(clientEntity.getVpnConfigureResult() != null
                        ? Result.fromString(clientEntity.getVpnConfigureResult())
                        : null
                )
                .build();
    }
}
