package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.commands.VelocityPlayer;
import eg.mqzen.guilds.util.GuildMessageFormatter;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.types.Context;
import studio.mevera.imperat.annotations.types.Dependency;
import studio.mevera.imperat.annotations.types.Description;
import studio.mevera.imperat.annotations.types.Execute;
import studio.mevera.imperat.annotations.types.Greedy;
import studio.mevera.imperat.annotations.types.SubCommand;

import java.util.Optional;

@SubCommand(value = {"chat", "c"})
@Description("Chat in your guild")
public class ChatSubCommand {

    @Dependency
    DominusGuilds plugin;

    @Execute
    public void defaultUsage(VelocityPlayer source) {
        source.reply("Usage: /guild chat <message>");
    }

    @Execute
    public void chat(VelocityPlayer source, @Greedy String message, @Context Guild<Player> sourceGuild) {
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