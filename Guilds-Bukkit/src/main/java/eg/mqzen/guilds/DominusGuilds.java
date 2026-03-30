package eg.mqzen.guilds;

import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DominusGuilds extends JavaPlugin {
    private @Nullable GuildManager guildManager;
    private BukkitGuildsRedisHandler redisHandler;
    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();

        guildManager = null;
        redisHandler = new BukkitGuildsRedisHandler(this, this.getConfig());
        redisHandler.initialize();
    }

    @Override
    public void onDisable() {
        if(redisHandler != null) {
            redisHandler.shutdown();
        }
    }

    public GuildManager getGuildCache() {
        return guildManager;
    }

    public void setGuildCache(@Nullable GuildManager guildManager) {
        this.guildManager = guildManager;
    }
}
