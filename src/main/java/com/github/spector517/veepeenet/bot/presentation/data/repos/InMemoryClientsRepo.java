package com.github.spector517.veepeenet.bot.presentation.data.repos;

import com.github.spector517.veepeenet.bot.presentation.data.dto.ClientEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Log4j2
@Repository
@RequiredArgsConstructor
public class InMemoryClientsRepo implements ClientsRepo {

    private final Map<Long, ClientEntity> clientEntityMap;

    @Override
    public Optional<ClientEntity> find(long telegramId) {
        var clientEntity = clientEntityMap.get(telegramId);
        if (clientEntity == null) {
            log.debug("Client not found in repository");
            return Optional.empty();
        }
        log.debug("Client found in repository");
        log.debug(clientEntity);
        return Optional.of(clientEntity);
    }

    @Override
    public ClientEntity save(ClientEntity clientEntity) {
        clientEntityMap.put(clientEntity.getTelegramId(), clientEntity);
        log.debug("Client saved to repository");
        log.debug(clientEntity);
        return clientEntity;
    }

    @Override
    public void delete(ClientEntity clientEntity) {
        clientEntityMap.remove(clientEntity.getTelegramId());
        log.debug("Client removed from repository");
        log.debug(clientEntity);
    }
}
