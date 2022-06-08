package redis.client;

import redis.client.commands.StringCommands;

public class Jedis implements StringCommands {
    protected final Connection connection;
    private final CommandObjects commandObjects = new CommandObjects();
//    private Transaction transaction = null;
    public Jedis(final String host, final int port) {
        connection = new Connection(host, port);
    }

    @Override
    public String set(final String key, final String value) {
//        checkIsInMultiOrPipeline();
        return connection.executeCommand(commandObjects.set(key, value));
    }

    @Override
    public String get(String key) {
        return null;
    }
//    protected void checkIsInMultiOrPipeline() {
////    if (connection.isInMulti()) {
//        if (transaction != null) {
//            throw new IllegalStateException(
//                    "Cannot use Jedis when in Multi. Please use Transaction or reset jedis state.");
//        } else if (pipeline != null && pipeline.hasPipelinedResponse()) {
//            throw new IllegalStateException(
//                    "Cannot use Jedis when in Pipeline. Please use Pipeline or reset jedis state.");
//        }
//    }
}
