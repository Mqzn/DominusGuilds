package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.commands.VelocityPlayer;
import studio.mevera.imperat.annotations.*;
import com.rivemc.guilds.Guild;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.util.GuildMessageFormatter;
import com.velocitypowered.api.proxy.Player;
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