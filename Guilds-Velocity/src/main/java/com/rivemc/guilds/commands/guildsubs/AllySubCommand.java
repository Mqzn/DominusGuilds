package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildRole;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.commands.GuildQueryResult;
import com.rivemc.guilds.commands.RequiredGuildPermissions;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.SubCommand;

@SubCommand("ally")
@RequiredGuildPermissions(GuildRole.Permission.MANAGE_GUILD_RELATIONS)
public class AllySubCommand {

    @Dependency
    private RiveGuilds plugin;

    @SubCommand("add")
    public void addAlly(VelocitySource source, @ContextResolved Guild<Player> sourceGuild, GuildQueryResult queryResult) {

        Guild<Player> foundGuild = queryResult.toGuild();
        if(foundGuild.getID().equals(sourceGuild.getID())) {
            source.error("You cannot ally with your own guild");
            return;
        }

        if(sourceGuild.isAlliedWith(foundGuild.getID())) {
            source.error("<red>You are already allied with " + foundGuild.getName());
            return;
        }

        sourceGuild.allyWith(foundGuild);
        plugin.getGuildManager().updateGuild(sourceGuild.getID(), sourceGuild, true)
                .sendMessageOnSuccess(source.origin(), "<gray>You are now allied with " + foundGuild.getTag().getMiniMessageFormattedTag());
    }

    @SubCommand("remove")
    public void removeAlly(VelocitySource source, @ContextResolved Guild<Player> sourceGuild, GuildQueryResult queryResult) {
        Guild<Player> foundGuild = queryResult.toGuild();

        if (!sourceGuild.isAlliedWith(foundGuild.getID())) {
            source.error("<red>You are not allied with " + foundGuild.getName());
            return;
        }

        sourceGuild.unAllyWith(foundGuild);
        plugin.getGuildManager().updateGuild(sourceGuild.getID(), sourceGuild, true)
                .sendMessageOnSuccess(source.origin(), "<gray>You are no longer allied with " + foundGuild.getTag().getMiniMessageFormattedTag());
    }

}
