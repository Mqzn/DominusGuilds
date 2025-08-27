package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.commands.VelocityPlayer;
import studio.mevera.imperat.annotations.*;
import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMember;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.database.GuildUpdateAction;
import com.velocitypowered.api.proxy.Player;

import java.util.Optional;

@SubCommand("leave")
@Description("Leave the guild")
public class LeaveSubCommand {
    
    @Dependency
    RiveGuilds plugin;

    @Usage
    public void defaultUsage(VelocityPlayer source, @ContextResolved Guild<Player> sourceGuild) {
        Optional<GuildMember<Player>> guildMemberOptional = sourceGuild.getMember(source.uuid());
        if (guildMemberOptional.isEmpty()) {
            source.reply("<red>Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        // Check if the source is the guild owner
        GuildMember<Player> guildMember = guildMemberOptional.get();
        if (guildMember.equals(sourceGuild.getOwner())) {
            source.reply("<red>You are the owner of the guild, You can use (/guild disband) instead.");
            return;
        }

        sourceGuild.removeMember(guildMember.getUUID());

        plugin.getGuildManager().updateGuild(GuildUpdateAction.REMOVE_MEMBER, sourceGuild.getID(), sourceGuild, true)
                .sendErrorMessage(source.origin(),"Failed to leave the guild, Something went wrong! \n Contact an admin to resolve this matter.")
                .onSuccess((g) -> {
                    sourceGuild.getMembers().forEach(member -> {
                        Optional<Player> guildPlayerOpt = plugin.getServer().getPlayer(member.getUUID());
                        guildPlayerOpt.ifPresent(guildPlayer -> 
                            guildPlayer.sendRichMessage(sourceGuild.getTag().getMiniMessageFormattedTag()
                                    + " <red>" + source.name() + " has left the guild.")
                        );
                    });
                    source.reply("<gray>You have left the guild <yellow>" + sourceGuild.getName() + "</yellow><gray>.");
                });
    }
}