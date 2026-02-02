package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.commands.VelocityPlayer;
import eg.mqzen.guilds.database.GuildUpdateAction;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

import java.util.Optional;

@SubCommand("leave")
@Description("Leave the guild")
public class LeaveSubCommand {
    
    @Dependency
    DominusGuilds plugin;

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