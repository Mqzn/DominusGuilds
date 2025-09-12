package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.Named;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.SuggestionProvider;
import studio.mevera.imperat.annotations.Usage;

import java.util.Optional;

@SubCommand("invite")
@Description("Invite a player to your guild")
public class InviteSubCommand {

    @Dependency
    RiveGuilds plugin;

    @Usage
    public void defaultUsage(VelocityPlayer source) {
        source.reply("Usage: /guild invite <player>");
    }

    @Usage
    public void inviteToOwnGuild(
            VelocityPlayer source,
            @ContextResolved Guild<Player> sourceGuild,
            @Named("target")
            @SuggestionProvider("non-guild-players") String target
    ) {
        // Check if the target player is online on the proxy
        Optional<Player> playerOpt = plugin.getServer().getPlayer(target);
        playerOpt.ifPresent(player ->
                plugin.getGuildManager().invitePlayerToGuild(
                    source.asPlayer(), sourceGuild, player
                )
        );
    }
}