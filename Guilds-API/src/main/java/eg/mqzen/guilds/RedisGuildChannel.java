package eg.mqzen.guilds;

import java.util.Arrays;

public enum RedisGuildChannel {

    EVENTS_SYNCHRONIZATION,

    LOAD_GUILD_CACHE;

    public final static String NAMESPACE_PREFIX = "eg.mqzen.guilds:";
    private final String loadedName;

    RedisGuildChannel() {
        this.loadedName = NAMESPACE_PREFIX + name().toLowerCase();
    }

    public static RedisGuildChannel fromName(String channel) {
        for (RedisGuildChannel c : RedisGuildChannel.values()) {
            if (c.loadedName.equals(channel)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown channel: " + channel);
    }

    public static String[] getAllChannelsNames() {
        return Arrays.stream(RedisGuildChannel.values()).map(RedisGuildChannel::getName).toArray(String[]::new);
    }

    public String getName() {
        return loadedName;
    }
}
