package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.DominusGuilds;
import com.velocitypowered.api.proxy.Player;

import studio.mevera.imperat.VelocityCommandSource;
import studio.mevera.imperat.annotations.types.Dependency;
import studio.mevera.imperat.annotations.types.Description;
import studio.mevera.imperat.annotations.types.Execute;
import studio.mevera.imperat.annotations.types.Named;
import studio.mevera.imperat.annotations.types.SubCommand;

import java.util.Optional;

@SubCommand("deny")
@Description("Deny an invitation to a guild.")
public class DenyInviteSubCommand {

    @Dependency
    DominusGuilds plugin;

    @Execute
    public void defaultUsage(VelocityCommandSource source) {
        source.reply("Usage: /guild deny <guild name>");
    }

    @Execute
    public void denyInvite(VelocityCommandSource source, @Named("guild-name") String guildName) {

        if(plugin.getGuildManager().getPlayerGuild(source.asPlayer().getUniqueId()).isPresent()) {
            source.reply("<red>You are already in a guild, Why are you trying to deny invites ?!");
            return;
        }

        // Find guild by name
        plugin.getGuildManager().getGuildByName(guildName).ifPresentOrElse(guild -> {
            // Check if player was invited
            if (!guild.getInviteList().isInvited(source.asPlayer().getUniqueId())) {
                source.reply("<red>You have not been invited to this guild!");
                return;
            }

            // Get inviter UUID to notify them
            guild.getInviteList().getInviter(source.asPlayer().getUniqueId()).ifPresent(inviterUUID -> {
                Optional<Player> inviterOpt = plugin.getServer().getPlayer(inviterUUID);
                inviterOpt.ifPresent(inviter -> 
                    inviter.sendRichMessage("<red>" + source.name() + " has denied your guild invitation!")
                );
                
                // Remove invite
                guild.getInviteList().removeInvite(source.asPlayer().getUniqueId());
            });

            // Notify player
            source.reply("<yellow>You have denied the invitation to join guild '" + guild.getName() + "'");
        }, () -> source.reply("<red>Guild '" + guildName + "' not found!"));
    }
}