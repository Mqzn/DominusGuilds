package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.commands.VelocityPlayer;
import studio.mevera.imperat.annotations.*;
import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMember;
import com.rivemc.guilds.GuildRole;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.database.GuildUpdateAction;
import com.velocitypowered.api.proxy.Player;

import java.util.Optional;

@SubCommand("kick")
@Description("Kick a guild member")
public class KickSubCommand {
    
    @Dependency
    RiveGuilds plugin;

    @Usage
    public void defaultUsage(VelocityPlayer source) {
        source.reply("Usage: /guild kick <player>");
    }

    @Usage
    public void kick(VelocityPlayer source, @Named("target") GuildMember<Player> target, @ContextResolved Guild<Player> sourceGuild) {
        Optional<GuildMember<Player>> sourceMemberOptional = sourceGuild.getMember(source.uuid());
        if (sourceMemberOptional.isEmpty()) {
            source.reply("<red>Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        GuildMember<Player> sourceMember = sourceMemberOptional.get();
        // Check if target is in a guild.
        Optional<Guild<Player>> targetGuildOptional = plugin.getGuildManager().getPlayerGuild(target.getUUID());
        if (targetGuildOptional.isEmpty()) {
            source.reply("<red>" + target.getName() + " player isn't in a guild.");
            return;
        }

        Guild<Player> targetGuild = targetGuildOptional.get();

        // Check if target is in the same guild as source.
        if (sourceGuild != targetGuild) {
            source.reply("<red>You are not in the same guild as " + target.getName() + ".");
            return;
        }

        Optional<GuildMember<Player>> targetMemberOptional = targetGuild.getMember(target.getUUID());
        if (targetMemberOptional.isEmpty()) {
            source.reply("<red>Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        GuildMember<Player> targetMember = targetMemberOptional.get();

        // Check if the source has permission to kick
        if (!sourceMember.hasPermission(sourceGuild, GuildRole.Permission.KICK_MEMBER)) {
            source.reply("<red>You don't have permissions to kick members.");
            return;
        }

        // Check if the source has a higher role than the target
        GuildRole sourceRole = sourceGuild.getMemberRole(sourceMember);
        GuildRole targetRole = sourceGuild.getMemberRole(targetMember);
        if (sourceRole.getWeight() <= targetRole.getWeight()) {
            source.reply("<red>You have to have a higher role than " + target.getName() + " to kick him.");
            return;
        }

        sourceGuild.removeMember(targetMember.getUUID());

        plugin.getGuildManager().updateGuild(GuildUpdateAction.REMOVE_MEMBER, sourceGuild.getID(), sourceGuild, true)
                .onSuccess((g)-> {
                    sourceGuild.getMembers().forEach(member -> {
                        Optional<Player> guildPlayerOpt = plugin.getServer().getPlayer(member.getUUID());
                        guildPlayerOpt.ifPresent(guildPlayer -> 
                            guildPlayer.sendRichMessage(sourceGuild.getTag().getMiniMessageFormattedTag()
                                    + " <red>" + source.name() + " has kicked " + target.getName())
                        );
                    });
                    Optional<Player> targetPlayerOpt = plugin.getServer().getPlayer(target.getUUID());
                    targetPlayerOpt.ifPresent(targetPlayer -> 
                        targetPlayer.sendRichMessage("<red>You have been kicked from " + sourceGuild.getTag().getMiniMessageFormattedTag() + " guild.")
                    );
                });
    }
}