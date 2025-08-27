package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.commands.VelocityPlayer;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;
import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMember;
import com.rivemc.guilds.GuildRole;
import com.rivemc.guilds.RiveGuilds;
import com.velocitypowered.api.proxy.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SubCommand("permissions")
@Description("Displays your guild permissions")
public class PermissionsSubCommand {
    
    @Dependency
    RiveGuilds plugin;

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