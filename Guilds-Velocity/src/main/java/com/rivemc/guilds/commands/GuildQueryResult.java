package com.rivemc.guilds.commands;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.RiveGuilds;
import com.velocitypowered.api.proxy.Player;

import java.util.UUID;

public record GuildQueryResult(RiveGuilds plugin, String rawInput, UUID guildIdFound, Type type) {

    public Guild<Player> toGuild() {
        return plugin.getGuildManager().getGuildByID(guildIdFound).orElseThrow();
    }

    enum Type {
        PLAYER_NAME,
        GUILD_NAME,
        GUILD_TAG
    }

}
