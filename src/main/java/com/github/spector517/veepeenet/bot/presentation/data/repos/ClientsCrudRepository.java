package com.github.spector517.veepeenet.bot.presentation.data.repos;

import com.github.spector517.veepeenet.bot.presentation.data.dto.ClientEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientsCrudRepository extends CrudRepository<ClientEntity, Long> {}
