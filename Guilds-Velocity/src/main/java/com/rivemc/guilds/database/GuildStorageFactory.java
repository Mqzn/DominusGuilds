package com.rivemc.guilds.database;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.database.storage.EmptyGuildStorage;
import com.rivemc.guilds.database.storage.MongoGuildStorage;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Locale;
import java.util.UUID;

/**
 * @author Mqzen
 * @date 18/5/2025
 */
public final class GuildStorageFactory implements StorageFactory<UUID, Guild<Player>, ConfigurationNode> {


    final EmptyGuildStorage DEV_STORAGE;

    private final RiveGuilds plugin;
    public GuildStorageFactory(RiveGuilds plugin) {
        try {
            this.plugin = plugin;
            DEV_STORAGE = new EmptyGuildStorage(plugin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull Storage<UUID, Guild<Player>> createStorage(@NotNull ConfigurationNode cfg) throws Exception {
        final Storage.Type type = Storage.Type.valueOf(cfg.node("storage", "type").getString(Storage.Type.EMPTY.name())
                .toUpperCase(Locale.ENGLISH));
        switch (type) {
            case MONGO -> {
                return new MongoGuildStorage(plugin, cfg.node("storage","connection","mongo"));
            }
            case SQLITE -> //return new SQLiteGuildStorage(cfg.getConfigurationSection("storage.connection.sqlite"));
                    throw new UnsupportedOperationException("SQLite storage is not implemented yet.");
            case MY_SQL -> //return new MySQLGuildStorage(cfg.getConfigurationSection("storage.connection.mysql"));
                    throw new UnsupportedOperationException("MySQL storage is not implemented yet.");
            default -> {
                return DEV_STORAGE;
            }
        }
    }
}