package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.commands.VelocityPlayer;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.Named;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;
import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMember;
import com.rivemc.guilds.GuildRole;
import com.rivemc.guilds.GuildTag;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.base.SimpleGuildColor;
import com.rivemc.guilds.database.GuildUpdateAction;
import com.velocitypowered.api.proxy.Player;
import java.util.Optional;

@SubCommand("tagcolor")
@Description("Sets color to tag")
public class TagColorSubCommand {
    
    @Dependency
    RiveGuilds plugin;

    @Usage
    public void defaultUsage(VelocityPlayer source){
        source.reply("Usage: /guild tagcolor <color>");
        StringBuilder colorsMessage = new StringBuilder("Available colors: ");
        SimpleGuildColor[] colors = SimpleGuildColor.values();
        for (int i = 0; i < colors.length; i++) {
            colorsMessage.append(colors[i].getName());
            if (i < colors.length - 1)
                colorsMessage.append(", ");
        }
        source.reply(colorsMessage.toString());
    }

    @Usage
    public void tagcolor(VelocityPlayer source, @Named("color") SimpleGuildColor color, @ContextResolved Guild<Player> sourceGuild){
        Optional<GuildMember<Player>> sourceMemberOptional = sourceGuild.getMember(source.uuid());
        if (sourceMemberOptional.isEmpty()) {
            source.reply("<red>Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        GuildMember<Player> sourceMember = sourceMemberOptional.get();
        if (!sourceMember.hasPermission(sourceGuild, GuildRole.Permission.SET_TAG)) {
            source.reply("<red>You don't have enough permissions to modify the tag color!");
            return;
        }

        // Check if new color == old color
        if(sourceGuild.getTag().getColor().equals(color)){
            source.reply("<red>Nothing changed, you have to enter a new color.");
            return;
        }

        // Create new guild with updated tag color
        // remove old tag from the tracker
        String oldTagValue = sourceGuild.getTag().getPlainValue();
        plugin.getGuildManager().getDistinctTagTracker().removeTag(oldTagValue, sourceGuild.getID());

        sourceGuild.getTag().setColor(color);

        plugin.getGuildManager().updateGuild(GuildUpdateAction.CHANGE_TAG, sourceGuild.getID(), sourceGuild, true)
                .onSuccess((guild)-> {
                    GuildTag newTag = guild.getTag();

                    // add new tag to the tracker
                    plugin.getGuildManager().getDistinctTagTracker()
                            .addTagFrom(guild);

                    source.reply("<gray>Updated tag color of guild <yellow>" + guild.getName() +
                            "</yellow> to " + newTag.getMiniMessageFormattedTag() + " <gray>!");
                });
    }
}