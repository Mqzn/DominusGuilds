package com.rivemc.guilds.database;

import com.rivemc.guilds.config.Config;
import org.jetbrains.annotations.NotNull;

public interface StorageFactory<K, D, C> {

    @NotNull Storage<K, D> createStorage(@NotNull C config) throws Exception;
}