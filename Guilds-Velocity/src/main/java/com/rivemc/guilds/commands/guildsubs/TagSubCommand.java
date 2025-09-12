package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMember;
import com.rivemc.guilds.GuildRole;
import com.rivemc.guilds.GuildTag;
import com.rivemc.guilds.RiveGuilds;
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

@SubCommand("tag")
@Description("Sets guild tag")
public class TagSubCommand {
    
    @Dependency
    RiveGuilds plugin;

    @Usage
    public void defaultUsage(VelocityPlayer source){
        source.reply("Usage: /guild tag <tag>");
    }

    @Usage
    public void tag(VelocityPlayer source, @Named("tag") String tag, @ContextResolved Guild<Player> sourceGuild){
        if (tag == null || tag.isEmpty()) return;

        Optional<GuildMember<Player>> sourceMemberOptional = sourceGuild.getMember(source.uuid());
        if (sourceMemberOptional.isEmpty()) {
            source.reply("<red>Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        GuildMember<Player> sourceMember = sourceMemberOptional.get();
        if (!sourceMember.hasPermission(sourceGuild, GuildRole.Permission.SET_TAG)) {
            source.reply("<red>You don't have enough permissions to modify the tag!");
            return;
        }

        // Check if new tag == old tag
        if(sourceGuild.getTag().getPlainValue().equals(tag)){
            source.reply("<red>Nothing changed, you have to enter a new tag.");
            return;
        }

        // Check if new tag > 4 characters
        if(tag.length() > 4){
            source.reply("<red>Tag name can only have 4 letters e.g. PAVE");
            return;
        }

        // Check if the tag is taken
        if(plugin.getGuildManager().getDistinctTagTracker().isTaken(tag)){
            source.reply("<red>This tag is taken.");
            return;
        }

        String oldTagValue = sourceGuild.getTag().getPlainValue();
        sourceGuild.getTag().setPlainValue(tag);
        plugin.getGuildManager().updateGuild(GuildUpdateAction.CHANGE_TAG, sourceGuild.getID(), sourceGuild, true)
                .onSuccess((g)-> {
                   GuildTag newTag = g.getTag();
                   plugin.getGuildManager().getDistinctTagTracker().updateTag(oldTagValue, newTag.getPlainValue(), sourceGuild.getID());
                   source.reply("<gray>Updated tag of guild <yellow>" + g.getName() + "</yellow> to '" + newTag.getMiniMessageFormattedTag() + "<yellow>'");
                });
    }
}