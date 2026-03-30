package eg.mqzen.guilds;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.bukkit.configuration.file.FileConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class BukkitGuildsRedisHandler extends JedisPubSub implements GuildsRedisHandler {

    private JedisPool jedisPool;
    private final String redisHost;
    private final int redisPort;
    private final String redisPassword;
    private final int redisDatabase;
    private final int redisTimeout;
    private final DominusGuilds plugin;
    private final Kryo kryo;

    public BukkitGuildsRedisHandler(DominusGuilds plugin, String host, int port, String password, int database, int timeout) {
        this.plugin = plugin;
        this.redisHost = host;
        this.redisPort = port;
        this.redisPassword = password;
        this.redisDatabase = database;
        this.redisTimeout = timeout;
        this.kryo = new Kryo();
    }

    public BukkitGuildsRedisHandler(DominusGuilds plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.redisHost = config.getString("redis.host");
        this.redisPort = config.getInt("redis.port");
        this.redisPassword = config.getString("redis.password");
        this.redisDatabase = config.getInt("redis.database");
        this.redisTimeout = config.getInt("redis.timeout");
        this.kryo = new Kryo();
    }

    public BukkitGuildsRedisHandler(DominusGuilds plugin) {
        this(plugin, plugin.getConfig());
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
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.subscribe(this, RedisGuildChannel.LOAD_GUILD_CACHE.getName());
        }
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



    @Override
    public final void onMessage(String channel, String message) {
        super.onMessage(channel, message);
        this.receive(RedisGuildChannel.fromName(channel), message);
    }

    public void receive(RedisGuildChannel channel, String message) {
        switch (channel) {
            case EVENTS_SYNCHRONIZATION -> {
                throw new UnsupportedOperationException("EVENTS_SYNCHRONIZATION is not supported on the publisher side");
            }
            case LOAD_GUILD_CACHE -> {
                try(ByteArrayInputStream in = new ByteArrayInputStream(message.getBytes())) {
                    var guildManager = kryo.readObject(new com.esotericsoftware.kryo.io.Input(in), GuildManager.class);
                    plugin.setGuildCache(guildManager);
                } catch (Exception e) {
                    System.err.println("Error processing LOAD_GUILD_CACHE message: " + e.getMessage());
                }
            }
            default -> {
                throw new IllegalArgumentException("Unknown channel '" + channel.name() + "'");
            }
        }
    }
}
