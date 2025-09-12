package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.commands.VelocityPlayer;
import com.rivemc.guilds.util.GuildMessageFormatter;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.Greedy;
import studio.mevera.imperat.annotations.Named;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

import java.util.Optional;

@SubCommand(value = {"chat", "c"})
@Description("Chat in your guild")
public class ChatSubCommand {

    @Dependency
    RiveGuilds plugin;

    @Usage
    public void defaultUsage(VelocityPlayer source) {
        source.reply("Usage: /guild chat <message>");
    }

    @Usage
    public void chat(VelocityPlayer source, @Greedy @Named("message") String message, @ContextResolved Guild<Player> sourceGuild) {
        sourceGuild.getMembers().forEach(member -> {
            Optional<Player> playerOpt = plugin.getServer().getPlayer(member.getUUID());
            playerOpt.ifPresent(player ->
                    sourceGuild.broadcast(plugin.getGuildManager(),
                            GuildMessageFormatter.formatGuildChatMessage(sourceGuild, source.asPlayer(), message)
                    )
            );
        });
    }
}