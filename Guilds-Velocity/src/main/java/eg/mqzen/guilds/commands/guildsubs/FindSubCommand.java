package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.Named;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SubCommand("find")
@Description("Find Info on a player's guild")
public class FindSubCommand {
    
    @Dependency
    DominusGuilds plugin;

    @Usage
    public void defaultUsage(VelocityPlayer source) {
        source.reply("Usage: /guild find <player>");
    }

    @Usage
    public void find(VelocityPlayer source, @Named("target") Player target) {
        if (source.asPlayer() == target) {
            source.reply("<red>Please use (/guild info) instead.");
            return;
        }

        // Check if target is in a guild.
        Optional<Guild<Player>> guildOptional = plugin.getGuildManager().getPlayerGuild(target.getUniqueId());
        if (guildOptional.isEmpty()) {
            source.reply("<yellow>" + target.getUsername() + " doesn't have a guild.");
            return;
        }

        Guild<Player> targetGuild = guildOptional.get();
        StringBuilder message = new StringBuilder("<gold>=== <yellow><bold>" +
                targetGuild.getName() + "</bold></yellow> <gold>===\n");
        message.append("<green>Owner: </green><white>").append(targetGuild.getOwnerInfo().getName()).append("</white>\n");
        message.append("<green>").append(target.getUsername()).append(": </green>")
                .append("<white>").append(targetGuild.getMemberRole(target.getUniqueId()).getPrefix()).append("</white>\n");
        message.append("<green>Members: </green><white>\n");
        List<GuildMember<Player>> guildMembers = new ArrayList<>(targetGuild.getMembers());
        // Build member list string
        String memberList = guildMembers.stream()
                .map(member -> {
                    Optional<Player> playerOpt = plugin.getServer().getPlayer(member.getUUID());
                    boolean isOnline = playerOpt.isPresent();
                    return isOnline ?
                            "<green>" + member.getName() :
                            "<gray>" + member.getName();
                })
                .collect(Collectors.joining("<white>, </white>"));
        message.append(memberList);

        source.reply(message.toString());
    }
}