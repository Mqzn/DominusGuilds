package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMember;
import com.rivemc.guilds.GuildRole;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.base.SimpleGuildMember;
import com.rivemc.guilds.commands.VelocityPlayer;
import com.rivemc.guilds.database.GuildUpdateAction;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.Named;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

import java.util.Optional;

@SubCommand(value = {"setrole"})
@Description("Sets a role for a player.")
public class SetRoleSubCommand {
    
    @Dependency
    RiveGuilds plugin;

    @Usage
    public void defaultUsage(VelocityPlayer source) {
        source.reply("Usage: /guild setrole <player> <role>");
    }

    @Usage
    public void setRole(VelocityPlayer source, @Named("target") Player target, @Named("role") String role, @ContextResolved Guild<Player> sourceGuild) {
        if (source == target) return;

        // Check if target is in a guild.
        Optional<Guild<Player>> targetGuildOptional = plugin.getGuildManager().getPlayerGuild(target.getUniqueId());
        if (targetGuildOptional.isEmpty()) {
            source.reply("<red>" + target.getUsername() + " player isn't in a guild.");
            return;
        }

        Guild<Player> targetGuild = targetGuildOptional.get();

        // Check if target is in the same guild as source.
        if (sourceGuild != targetGuild) {
            source.reply("<red>You are not in the same guild as " + target.getUsername() + ".");
            return;
        }

        // Check if source has permissions
        Optional<GuildMember<Player>> sourceOptional = sourceGuild.getMember(source.uuid());
        Optional<GuildMember<Player>> targetOptional = sourceGuild.getMember(target.getUniqueId());
        if (sourceOptional.isEmpty() || targetOptional.isEmpty()) {
            source.reply("<red>Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        GuildMember<Player> sourceGuildMember = sourceOptional.get();
        GuildMember<Player> targetGuildMember = targetOptional.get();
        if (!sourceGuildMember.hasPermission(sourceGuild, GuildRole.Permission.SET_ROLE_MEMBER)) {
            source.reply("<red>You don't have enough permissions to modify roles!");
            return;
        }

        // Check if an argument role is viable
        Optional<GuildRole> guildRoleOptional = sourceGuild.getRoleByName(role);
        if (guildRoleOptional.isEmpty()) {
            source.reply("<red>No such role name, check role names using /guild info");
            return;
        }

        // Argument guild role (supposed to be the new role of the target member)
        GuildRole newGuildRole = guildRoleOptional.get();

        // Check if argument role = target member role.
        if (newGuildRole == targetGuild.getMemberRole(targetGuildMember)) {
            source.reply("<red>Nothing changed, " + target.getUsername() + " is already a " + newGuildRole.getName());
            return;
        }

        // Check if argument role is owner
        if (newGuildRole == sourceGuild.getMemberRole(sourceGuild.getOwner())) {
            source.reply("<red>You can't set a player's role into the owner's");
            source.reply("<red>If you wanna transfer the guild, use /guild transfer <player>");
            return;
        }

        // Lastly, change the target guild member role into the new one.
        GuildMember<Player> newTargetMember = new SimpleGuildMember(
                target.getUniqueId(),
                target.getUsername(),
                newGuildRole.getID()
        );
        sourceGuild.removeMember(target.getUniqueId());
        sourceGuild.addMember(newTargetMember);

        plugin.getGuildManager().updateGuild(GuildUpdateAction.MEMBER_ROLE_SET, sourceGuild.getID(), sourceGuild, true)
                .sendErrorMessage(source.origin(), "<red>Failed to update guild member role, please try again later.")
                .onSuccess(()-> {
                    source.reply("<green>You changed " + target.getUsername() + " role to " + newGuildRole.getName());
                    Optional<Player> targetPlayerOpt = plugin.getServer().getPlayer(target.getUniqueId());
                    targetPlayerOpt.ifPresent(targetPlayer -> 
                        targetPlayer.sendRichMessage("<green>" + source.name() + " changed your role to " + newGuildRole.getName())
                    );
                });
    }
}