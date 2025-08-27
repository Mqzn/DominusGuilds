package com.rivemc.guilds.base;

import com.rivemc.guilds.GuildOwnerInfo;

import java.util.UUID;

public final class SimpleGuildOwnerInfo implements GuildOwnerInfo {

    private final String name;
    private final UUID uuid;

    public SimpleGuildOwnerInfo(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String toString() {
        return name + ":" + uuid;
    }

    public static SimpleGuildOwnerInfo fromString(String serializedInfo) {
        String[] parts = serializedInfo.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid serialized GuildOwnerInfo format");
        }
        String name = parts[0];
        UUID uuid = UUID.fromString(parts[1]);
        return new SimpleGuildOwnerInfo(name, uuid);
    }

}
