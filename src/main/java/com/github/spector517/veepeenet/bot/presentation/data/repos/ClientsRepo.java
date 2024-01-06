package com.github.spector517.veepeenet.bot.presentation.data.repos;

import com.github.spector517.veepeenet.bot.presentation.data.dto.ClientEntity;

import java.util.Optional;

public interface ClientsRepo {

    Optional<ClientEntity> find(long telegramId);

    ClientEntity save(ClientEntity clientEntity);

    void delete(ClientEntity clientEntity);
}
