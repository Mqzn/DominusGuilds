package com.rivemc.guilds.commands.guildsubs;

import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Named;
import studio.mevera.imperat.annotations.Usage;
import com.rivemc.guilds.RiveGuilds;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.VelocitySource;
import java.util.Optional;

@SubCommand("deny")
@Description("Deny an invitation to a guild.")
public class DenyInviteSubCommand {

    @Dependency
    RiveGuilds plugin;

    @Usage
    public void defaultUsage(VelocitySource source) {
        source.reply("Usage: /guild deny <guild name>");
    }

    @Usage
    public void denyInvite(VelocitySource source, @Named("guild-name") String guildName) {

        if(plugin.getGuildManager().getPlayerGuild(source.uuid()).isPresent()) {
            source.reply("<red>You are already in a guild, Why are you trying to deny invites ?!");
            return;
        }

        // Find guild by name
        plugin.getGuildManager().getGuildByName(guildName).ifPresentOrElse(guild -> {
            // Check if player was invited
            if (!guild.getInviteList().isInvited(source.uuid())) {
                source.reply("<red>You have not been invited to this guild!");
                return;
            }

            // Get inviter UUID to notify them
            guild.getInviteList().getInviter(source.uuid()).ifPresent(inviterUUID -> {
                Optional<Player> inviterOpt = plugin.getServer().getPlayer(inviterUUID);
                inviterOpt.ifPresent(inviter -> 
                    inviter.sendRichMessage("<red>" + source.name() + " has denied your guild invitation!")
                );
                
                // Remove invite
                guild.getInviteList().removeInvite(source.uuid());
            });

            // Notify player
            source.reply("<yellow>You have denied the invitation to join guild '" + guild.getName() + "'");
        }, () -> source.reply("<red>Guild '" + guildName + "' not found!"));
    }
}