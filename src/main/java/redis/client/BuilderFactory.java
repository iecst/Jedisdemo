package redis.client;

import redis.client.util.SafeEncoder;

public final class BuilderFactory {


    public static final Builder<String> STRING = new Builder<String>() {
        @Override
        public String build(Object data) {
            return data == null ? null : SafeEncoder.encode((byte[]) data);
        }

        @Override
        public String toString() {
            return "String";
        }

    };
}
