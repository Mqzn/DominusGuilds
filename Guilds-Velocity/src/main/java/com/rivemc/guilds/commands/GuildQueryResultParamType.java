package com.rivemc.guilds.commands;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.RiveGuilds;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.command.parameters.type.BaseParameterType;
import studio.mevera.imperat.context.ExecutionContext;
import studio.mevera.imperat.context.internal.CommandInputStream;
import studio.mevera.imperat.exception.ImperatException;

public class GuildQueryResultParamType extends BaseParameterType<VelocitySource, GuildQueryResult> {

    private final RiveGuilds plugin;
    public GuildQueryResultParamType(RiveGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable GuildQueryResult resolve(
            @NotNull ExecutionContext<VelocitySource> context,
            @NotNull CommandInputStream<VelocitySource> inputStream,
            @NotNull String input
    ) throws ImperatException {
        //check if its player name and find guild from the player's guild if its a player
        Player player = plugin.getServer().getPlayer(input).orElse(null);
        if(player != null) {
            return plugin.getGuildManager().getPlayerGuild(player)
                    .map((guild)-> new GuildQueryResult(plugin, input, guild.getID(), GuildQueryResult.Type.PLAYER_NAME))
                    .orElseThrow(()-> new TargetNotInAnyGuildException(context, input));

        }

        //then check if its a guild name
        Guild<Player> guild = plugin.getGuildManager().getGuildByName(input).orElse(null);
        if(guild != null) {
            return new GuildQueryResult(plugin, input, guild.getID(), GuildQueryResult.Type.GUILD_NAME);
        }

        //finally check if its a guild tag
        return plugin.getGuildManager().getDistinctTagTracker().getGuildIdByTag(input)
                .map((guildId) -> new GuildQueryResult(plugin, input, guildId, GuildQueryResult.Type.GUILD_TAG))
                .orElseThrow(()-> new UnknownGuildTagException(context, input));
    }
}
