package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.GuildRole;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.commands.VelocityPlayer;
import eg.mqzen.guilds.database.GuildUpdateAction;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.Named;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

import java.util.Optional;

@SubCommand("rename")
@Description("Change the guild's name")
public class RenameSubCommand {
    
    @Dependency
    DominusGuilds plugin;

    @Usage
    public void defaultUsage(VelocityPlayer source) {
        source.reply("Usage: /guild rename <name>");
    }

    @Usage
    public void rename(VelocityPlayer source, @Named("new-name") String newName, @ContextResolved Guild<Player> sourceGuild){
        // Check if player has permission to rename the guild
        Optional<GuildMember<Player>> guildMemberOptional = sourceGuild.getMember(source.uuid());
        if(guildMemberOptional.isEmpty()){
            source.reply("<red>Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        GuildMember<Player> guildMember = guildMemberOptional.get();
        if(!guildMember.hasPermission(sourceGuild, GuildRole.Permission.RENAME_GUILD)){
            source.reply("<red>You don't have permissions to rename the guild.");
            return;
        }

        // Check if the name is already taken
        if(plugin.getGuildManager().getGuildByName(newName).isPresent()){
            source.reply("<red>This name is taken.");
            return;
        }

        // cache old name
        String oldName = sourceGuild.getName();
        sourceGuild.setName(newName);
        plugin.getGuildManager().updateGuild(GuildUpdateAction.CHANGE_NAME, sourceGuild.getID(), sourceGuild, true)
                .onSuccess((updatedGuild)-> source.reply("<gray>Updated guild name <yellow>" +
                        oldName + "</yellow> to '<yellow>" + updatedGuild.getName() + "</yellow><gray>'"));
    }
}