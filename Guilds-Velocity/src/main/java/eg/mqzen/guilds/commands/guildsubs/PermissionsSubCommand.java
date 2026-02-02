package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.GuildRole;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SubCommand("permissions")
@Description("Displays your guild permissions")
public class PermissionsSubCommand {
    
    @Dependency
    DominusGuilds plugin;

    @Usage
    public void permissions(VelocityPlayer source, @ContextResolved Guild<Player> sourceGuild) {
        // Check if player is in a guild.

        Optional<GuildMember<Player>> guildMemberOptional = sourceGuild.getMember(source.uuid());
        if(guildMemberOptional.isEmpty()){
            source.reply("<red>Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        GuildMember<Player> guildMember = guildMemberOptional.get();
        GuildRole guildRole = sourceGuild.getMemberRole(guildMember);
        Set<GuildRole.Permission> permissions = new HashSet<>(guildRole.getPermissions());

        if(permissions.isEmpty()){
            source.reply("<yellow>You don't have any permissions.");
            return;
        }

        // Send the player a list of his role permissions.
        StringBuilder message = new StringBuilder(guildRole.getPrefix());
        message.append(" <gray>Your permissions are: \n<reset>");
        for (GuildRole.Permission permission : permissions) {
            message.append("<green>").append(permission.getValue()).append("\n");
        }

        source.reply(message.toString());
    }
}