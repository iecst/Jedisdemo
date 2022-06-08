package redis.client;

import redis.client.commands.ProtocolCommand;

public class CommandObjects {

    protected CommandArguments commandArguments(ProtocolCommand command) {
        return new CommandArguments(command);
    }

    public final CommandObject<String> set(String key, String value) {
        return new CommandObject<>(commandArguments(Protocol.Command.SET).key(key).add(value), BuilderFactory.STRING);
    }
}
