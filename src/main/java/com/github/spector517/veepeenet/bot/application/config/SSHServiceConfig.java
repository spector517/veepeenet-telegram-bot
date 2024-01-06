package com.github.spector517.veepeenet.bot.application.config;

import com.github.spector517.veepeenet.bot.properties.AppProperties;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Log4j2
@Configuration
public class SSHServiceConfig {

    @Bean
    @SneakyThrows
    public InputStream deployVpnScriptInputStream(AppProperties appProperties) {
        log.info("Download VPN deploy script...");
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(appProperties.getDeployVpnScriptUrl()))
                .GET()
                .build();
        try (var httpClient = HttpClient.newHttpClient()) {
            var response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofByteArray()
            );
            if (response.statusCode() != 200) {
                log.error("Error when download resource from '{}', status: {}",
                        response.uri(),
                        response.statusCode()
                );
                System.exit(1);
            }
            log.info("Deploy VPN script downloaded.");
            return new ByteArrayInputStream(response.body());
        }
    }
}
