package eg.mqzen.guilds.commands;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.DominusGuilds;
import com.velocitypowered.api.proxy.Player;

import java.util.UUID;

public record GuildQueryResult(DominusGuilds plugin, String rawInput, UUID guildIdFound, Type type) {

    public Guild<Player> toGuild() {
        return plugin.getGuildManager().getGuildByID(guildIdFound).orElseThrow();
    }

    enum Type {
        PLAYER_NAME,
        GUILD_NAME,
        GUILD_TAG
    }

}
