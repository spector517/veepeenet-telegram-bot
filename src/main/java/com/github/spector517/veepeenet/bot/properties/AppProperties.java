package com.github.spector517.veepeenet.bot.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private static final int MIN_TICK_RATE = 1;
    private static final int MAX_TICK_RATE = 20;

    private String name;
    private String token;
    @Min(1) @Max(20)
    private int tickRate;
    private String deployVpnScriptUrl;
    private int sshConnectionTimeout;
    @Min(100)
    private int sshExecTimeout;
    private String vpnScriptRemotePath;
    private String vpnClientsRemoteDir;
    private String remotePythonInterpreter;

    public long getSleepingInterval() {
        return Math.ceilDiv(1000, tickRate);
    }
}
