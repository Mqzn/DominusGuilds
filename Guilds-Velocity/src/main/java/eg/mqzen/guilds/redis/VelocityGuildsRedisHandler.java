package eg.mqzen.guilds.redis;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import eg.mqzen.guilds.GuildsRedisHandler;
import eg.mqzen.guilds.RedisGuildChannel;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.io.ByteArrayOutputStream;

/**
 * Manages Redis connections and publishes guild-related events to Redis channels.
 * This is the Velocity-side publisher that sends authoritative guild state to Bukkit servers.
 */
public class VelocityGuildsRedisHandler extends JedisPubSub implements GuildsRedisHandler {

    private JedisPool jedisPool;
    private final String redisHost;
    private final int redisPort;
    private final String redisPassword;
    private final int redisDatabase;
    private final int redisTimeout;

    private final Kryo kryo;
    public VelocityGuildsRedisHandler(String host, int port, String password, int database, int timeout) {
        this.redisHost = host;
        this.redisPort = port;
        this.redisPassword = password;
        this.redisDatabase = database;
        this.redisTimeout = timeout;
        this.kryo = new Kryo();
    }

    /**
     * Initializes the Redis connection pool.
     */
    public void initialize() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        var pass = redisPassword != null && !redisPassword.isEmpty() ? redisPassword : null;
        jedisPool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, pass, redisDatabase);
        jedisPool.getResource().subscribe(this, RedisGuildChannel.EVENTS_SYNCHRONIZATION.getName());
    }

    /**
     * Shuts down the Redis connection pool.
     */
    public void shutdown() {
//        if (asyncExecutor != null && !asyncExecutor.isShutdown()) {
//            asyncExecutor.shutdownNow();
//        }
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
    /**
     * Synchronously publishes a message to a Redis channel.
     *
     * @param channel the Redis channel to publish to
     * @param payload the object to serialize and publish
     */
    public void publish(RedisGuildChannel channel, Object payload) {
        try (var jedis = jedisPool.getResource(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            kryo.writeObject(new Output(out), payload);
            jedis.publish(channel.getName(), out.toString());
        } catch (Exception e) {
            System.err.println("Error publishing to Redis channel " + channel + ": " + e.getMessage());
        }
    }

    public void receive(RedisGuildChannel channel, String message) {
        switch (channel) {
            case EVENTS_SYNCHRONIZATION -> {
                //TODO IMPL
            }
            case LOAD_GUILD_CACHE -> {
                throw new UnsupportedOperationException("LOAD_GUILD_CACHE is not supported on the publisher side");
            }
            default -> {
                throw new IllegalArgumentException("Unknown channel '" + channel.name() + "'");
            }
        }
    }

    @Override
    public final void onMessage(String channel, String message) {
        super.onMessage(channel, message);
        this.receive(RedisGuildChannel.fromName(channel), message);
    }
}