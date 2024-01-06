package com.github.spector517.veepeenet.bot.presentation.adapters.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.log4j.Log4j2;

import java.io.InputStream;
import java.util.concurrent.TimeoutException;

@Log4j2
public class SSHRemoteConnector implements AutoCloseable {

    private static final int DEFAULT_SSH_PORT = 22;
    private static final int SAMPLING = 100;

    private final ConnectionData connectionData;

    private final Session session;
    private final ChannelExec channelExec;
    private final ChannelSftp channelSftp;
    private final int execTimeout;

    public record ConnectionData(String host, String user, String password){}

    public SSHRemoteConnector(ConnectionData connectionData, int sshTimeout, int execTimeout) {
        this.connectionData = connectionData;
        this.execTimeout =  execTimeout;
        try {
            session = new JSch().getSession(connectionData.user(), connectionData.host(), DEFAULT_SSH_PORT);
            session.setPassword(connectionData.password());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(sshTimeout);
            session.connect();
            channelExec = (ChannelExec) session.openChannel("exec");
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
        } catch (Exception exception) {
            throw new SSHConnectionException(exception.getMessage(), connectionData);
        }
    }

    @Override
    public void close() {
        if (channelSftp != null && channelSftp.isConnected()) {
            channelSftp.disconnect();
        }
        if (channelExec != null && channelExec.isConnected()) {
            channelExec.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    public void putFile(String filePath, InputStream inputStream) {
        try {
            channelSftp.put(inputStream, filePath);
        } catch (Exception exception) {
            throw new SSHConnectionException(exception.getMessage(), connectionData);
        }
    }

    public byte[] fetchFile(String filePath) {
        try {
            return channelSftp.get(filePath).readAllBytes();
        } catch (Exception exception) {
            throw new SSHConnectionException(exception.getMessage(), connectionData);
        }
    }

    public int runCommand(String command) {
        channelExec.setCommand(command);
        try {
            channelExec.connect();
            var maxAttemptsCount = Math.ceilDiv(execTimeout, SAMPLING);
            while (channelExec.isConnected() && maxAttemptsCount > 0) {
                Thread.sleep(SAMPLING);
                maxAttemptsCount--;
            }
            if (channelExec.isConnected()) {
                throw new TimeoutException("Exec command timeout. Command '%s'".formatted(command));
            }
            return channelExec.getExitStatus();
        } catch (Exception exception) {
            throw new SSHConnectionException(exception.getMessage(), connectionData);
        }
    }
}
