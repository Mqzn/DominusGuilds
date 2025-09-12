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

@SubCommand("enemy")
@RequiredGuildPermissions(GuildRole.Permission.MANAGE_GUILD_RELATIONS)
public class EnemySubCommand {

    @Dependency
    private RiveGuilds plugin;

    @SubCommand("add")
    public void addEnemy(VelocitySource source, @ContextResolved Guild<Player> sourceGuild, GuildQueryResult queryResult) {
        Guild<Player> foundGuild = queryResult.toGuild();

        if(foundGuild.getID().equals(sourceGuild.getID())) {
            source.error("<red>You cannot enemy with your own guild");
            return;
        }

        // Remove alliance if exists
        if (sourceGuild.isAlliedWith(foundGuild.getID())) {
            sourceGuild.unAllyWith(foundGuild);
        }

        if(sourceGuild.isEnemyWith(foundGuild.getID())) {
            source.error("<red>You are already enemies with " + foundGuild.getName());
            return;
        }

        sourceGuild.enemyWith(foundGuild);
        plugin.getGuildManager().updateGuild(sourceGuild.getID(), sourceGuild, true)
                .sendMessageOnSuccess(source.origin(), "<red>You are now enemies with " + foundGuild.getTag().getMiniMessageFormattedTag());
    }

    @SubCommand("remove")
    public void removeEnemy(VelocitySource source, @ContextResolved Guild<Player> sourceGuild, GuildQueryResult queryResult) {
        Guild<Player> foundGuild = queryResult.toGuild();
        if(foundGuild.getID().equals(sourceGuild.getID())) {
            source.error("<red>You cannot un-enemy with your own guild");
            return;
        }

        if (!sourceGuild.isEnemyWith(foundGuild.getID())) {
            source.error("<red>You are not enemies with " + foundGuild.getName());
            return;
        }

        sourceGuild.unEnemyWith(foundGuild);
        plugin.getGuildManager().updateGuild(sourceGuild.getID(), sourceGuild, true)
                .sendMessageOnSuccess(source.origin(), "<gray>You are no longer enemies with " + foundGuild.getTag().getMiniMessageFormattedTag());
    }
}
