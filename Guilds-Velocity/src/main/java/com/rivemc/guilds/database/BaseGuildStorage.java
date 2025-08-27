package com.rivemc.guilds.database;


import com.rivemc.guilds.RiveGuilds;
import com.velocitypowered.api.proxy.Player;
import org.spongepowered.configurate.ConfigurationNode;

/**
 * @author Mqzen
 * @date 18/5/2025
 */
public abstract class BaseGuildStorage implements GuildStorage<Player> {

    private final RiveGuilds plugin;
    private final Type type;
    protected ConfigurationNode section;

    protected BaseGuildStorage(RiveGuilds plugin, Type type, ConfigurationNode section) throws Exception {
        this.plugin = plugin;
        this.section = section;
        this.type = type;
        connect(section);
    }

    @Override
    public Type type() {
        return type;
    }

    protected abstract void connect(ConfigurationNode section) throws Exception;
}