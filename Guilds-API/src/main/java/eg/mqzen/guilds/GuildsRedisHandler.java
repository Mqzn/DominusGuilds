package eg.mqzen.guilds;

public interface GuildsRedisHandler {

    void initialize();

    void shutdown();

    void publish(RedisGuildChannel channel, Object payload);

    void receive(RedisGuildChannel channel, String message);

}
