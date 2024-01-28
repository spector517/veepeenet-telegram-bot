package com.github.spector517.veepeenet.bot.presentation.data.repos;

import com.github.spector517.veepeenet.bot.presentation.data.dto.ClientEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Log4j2
@Repository
@RequiredArgsConstructor
public class ClientsJpaRepo implements ClientsRepo {

    private final ClientsCrudRepository clientsCrudRepository;

    @Override
    public Optional<ClientEntity> find(long telegramId) {
        var clientEntityOptional = clientsCrudRepository.findById(telegramId);
        if (clientEntityOptional.isEmpty()) {
            log.debug("Client not found in repository");
            return clientEntityOptional;
        }
        log.debug("Client found in repository");
        log.debug(clientEntityOptional.get());
        return clientEntityOptional;
    }

    @Override
    public void save(ClientEntity clientEntity) {
        clientsCrudRepository.save(clientEntity);
        log.debug("Client saved to repository");
        log.debug(clientEntity);
    }

    @Override
    public void delete(ClientEntity clientEntity) {
        clientsCrudRepository.delete(clientEntity);
        log.debug("Client removed from repository");
        log.debug(clientEntity);
    }
}
