package redis.client;

import redis.client.execeptions.JedisConnectionException;

import java.net.Socket;

/**
 * JedisSocketFactory: responsible for creating socket connections
 * from the within the Jedis client, the default socket factory will
 * create TCP sockets with the recommended configuration.
 * <p>
 * You can use a custom JedisSocketFactory for many use cases, such as:
 * - a custom address resolver
 * - a unix domain socket
 * - a custom configuration for you TCP sockets
 */
public interface JedisSocketFactory {

  Socket createSocket() throws JedisConnectionException;
}
