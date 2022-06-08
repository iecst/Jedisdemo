package redis.client;

import redis.client.execeptions.JedisConnectionException;
import redis.client.util.IOUtils;
import redis.client.util.RedisInputStream;
import redis.client.util.RedisOutputStream;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Connection implements Closeable {

    private final JedisSocketFactory socketFactory;

    private RedisOutputStream outputStream;
    private RedisInputStream inputStream;
    private Socket socket;
    private int infiniteSoTimeout = 0;
    private int soTimeout = 0;
    private boolean broken = false;

    @Override
    public void close() throws IOException {

    }


    public Connection(final String host, final int port) {
        this(new HostAndPort(host, port));
    }

    public Connection(final HostAndPort hostAndPort) {
        this(new DefaultJedisSocketFactory(hostAndPort));
    }

    public Connection(final JedisSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }


    public <T> T executeCommand(final CommandObject<T> commandObject) {
        final CommandArguments args = commandObject.getArguments();
        sendCommand(args);
        if (!args.isBlocking()) {
            return commandObject.getBuilder().build(getOne());
        } else {
            try {
                setTimeoutInfinite();
                return commandObject.getBuilder().build(getOne());
            } finally {
                rollbackTimeout();
            }
        }
    }

    public void rollbackTimeout() {
        try {
            socket.setSoTimeout(this.soTimeout);
        } catch (SocketException ex) {
            broken = true;
            throw new JedisConnectionException(ex);
        }
    }

    public void setTimeoutInfinite() {
        try {
            if (!isConnected()) {
                connect();
            }
            socket.setSoTimeout(infiniteSoTimeout);
        } catch (SocketException ex) {
            broken = true;
            throw new JedisConnectionException(ex);
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isBound() && !socket.isClosed() && socket.isConnected()
                && !socket.isInputShutdown() && !socket.isOutputShutdown();
    }

    protected void flush() {
        try {
            outputStream.flush();
        } catch (IOException ex) {
            broken = true;
            throw new JedisConnectionException(ex);
        }
    }


    public Object getOne() {
        flush();
        return readProtocolWithCheckingBroken();
    }
    protected Object readProtocolWithCheckingBroken() {
        if (broken) {
            throw new JedisConnectionException("Attempting to read from a broken connection");
        }

        try {
            return Protocol.read(inputStream);
        } catch (JedisConnectionException exc) {
            broken = true;
            throw exc;
        }
    }
    public void sendCommand(final CommandArguments args) {
        try {
            connect();
            Protocol.sendCommand(outputStream, args);
        } catch (JedisConnectionException ex) {
            /*
             * When client send request which formed by invalid protocol, Redis send back error message
             * before close connection. We try to read it to provide reason of failure.
             */
            try {
                String errorMessage = Protocol.readErrorLineIfPossible(inputStream);
                if (errorMessage != null && errorMessage.length() > 0) {
                    ex = new JedisConnectionException(errorMessage, ex.getCause());
                }
            } catch (Exception e) {
                /*
                 * Catch any IOException or JedisConnectionException occurred from InputStream#read and just
                 * ignore. This approach is safe because reading error message is optional and connection
                 * will eventually be closed.
                 */
            }
            // Any other exceptions related to connection?
            broken = true;
            throw ex;
        }
    }


    public void connect() throws JedisConnectionException {
        if (!isConnected()) {
            try {
                socket = socketFactory.createSocket();
                soTimeout = socket.getSoTimeout(); //?

                outputStream = new RedisOutputStream(socket.getOutputStream());
                inputStream = new RedisInputStream(socket.getInputStream());
            } catch (JedisConnectionException jce) {
                broken = true;
                throw jce;
            } catch (IOException ioe) {
                broken = true;
                throw new JedisConnectionException("Failed to create input/output stream", ioe);
            } finally {
                if (broken) {
                    IOUtils.closeQuietly(socket);
                }
            }
        }
    }
}
