package com.github.spector517.veepeenet.bot.presentation.config;

import com.github.spector517.veepeenet.bot.presentation.data.repos.ClientsRepo;
import com.github.spector517.veepeenet.bot.presentation.data.repos.InMemoryClientsRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class ReposConfig {

    @Bean
    public ClientsRepo clientsRepo() {
        return new InMemoryClientsRepo(new HashMap<>());
    }
}
