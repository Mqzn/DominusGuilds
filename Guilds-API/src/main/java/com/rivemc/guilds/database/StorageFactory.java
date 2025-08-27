package com.rivemc.guilds.database;

import org.jetbrains.annotations.NotNull;

public interface StorageFactory<K, D, C> {

    @NotNull Storage<K, D> createStorage(@NotNull C config) throws Exception;
}