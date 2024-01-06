package com.github.spector517.veepeenet.bot.presentation.adapters.ssh;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SSHConnectionException extends RuntimeException {

    private final String message;
    private final SSHRemoteConnector.ConnectionData connectionData;

    @Override
    public String toString() {
        return "SSH-operation failure '%s', %s".formatted(message, connectionData);
    }
}
