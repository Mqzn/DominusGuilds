package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.base.SimpleGuildMember;
import eg.mqzen.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.types.Dependency;
import studio.mevera.imperat.annotations.types.Description;
import studio.mevera.imperat.annotations.types.Named;
import studio.mevera.imperat.annotations.types.SubCommand;
import studio.mevera.imperat.annotations.types.Execute;

import java.util.Optional;

@SubCommand("join")
@Description("Accepts a guild invite")
public class JoinGuildSubCommand {

    @Dependency
    DominusGuilds plugin;

    @Execute
    public void defaultUsage(VelocityPlayer source) {
        source.reply("Usage: /guild join <guild>");
    }

    @Execute
    public void acceptInvite(VelocityPlayer source, @Named("guild") String guildName) {
        // Check if player is already in a guild
        if (plugin.getGuildManager().getPlayerGuild(source.asPlayer().getUniqueId()).isPresent()) {
            source.reply("<red>You are already in a guild!");
            return;
        }

        // Find guild by name
        plugin.getGuildManager().getGuildByName(guildName).ifPresentOrElse(guild -> {
            // Check if player was invited
            if (!guild.getInviteList().isInvited(source.asPlayer().getUniqueId())) {
                source.reply("<red>You have not been invited to this guild!");
                return;
            }

            // Create new member with default role
            GuildMember<Player> newMember = new SimpleGuildMember(
                    source.asPlayer().getUniqueId(),
                    source.name(),
                    guild.getDefaultRole().getID()
            );

            // Add member to guild
            guild.addMember(newMember);

            plugin.getGuildManager().updateGuild(guild.getID(), guild, true)
                    .sendErrorMessage((CommandSource) source.origin(), "<red>An error occurred while accepting the invite!")
                    .onSuccess((updatedGuild) -> {
                        source.reply("<green>You have joined guild '" + guild.getName() + "'!");
                        updatedGuild.getMembers().forEach(member -> {
                            Optional<Player> playerOpt = plugin.getServer().getPlayer(member.getUUID());
                            playerOpt.ifPresent(player -> 
                                player.sendRichMessage("<green>" + source.name() + " has joined the guild!")
                            );
                        });

                        // Remove invite
                        updatedGuild.getInviteList().removeInvite(source.asPlayer().getUniqueId());
                    });

        }, () -> source.reply("<red>Guild '" + guildName + "' not found!"));
    }
}