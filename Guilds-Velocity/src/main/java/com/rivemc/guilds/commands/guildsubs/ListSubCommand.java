package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMember;
import com.rivemc.guilds.GuildRole;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SubCommand("list")
@Description("Displays the guild members")
public class ListSubCommand {

    @Dependency
    RiveGuilds plugin;

    @Usage
    public void listGuildMembers(VelocityPlayer source, @ContextResolved Guild<Player> guild) {
        // Group members by their roles
        Map<GuildRole, java.util.List<GuildMember<Player>>> membersByRole = guild.getMembers().stream()
                .collect(Collectors.groupingBy(member -> guild.getRoleByID(member.getRoleId())
                        .orElseThrow()));

        // Send header
        source.reply("");
        source.reply("<gold>=== <yellow>" + guild.getName() +
                "</yellow> <gold>Members (" + guild.getMembers().size() + ") ===");
        source.reply("");

        // Sort roles by weight/importance and display members
        guild.getRoles().stream()
                .sorted((r1, r2) -> Integer.compare(r2.getWeight(), r1.getWeight()))
                .forEach(role -> {
                    List<GuildMember<Player>> roleMembers = membersByRole.get(role);
                    if (roleMembers != null && !roleMembers.isEmpty()) {
                        // Send role header
                        source.reply("<gold>• " + role.getName() +
                                " <gray>(" + roleMembers.size() + ")</gray>");

                        // Build member list string
                        String memberList = roleMembers.stream()
                                .map(member -> {
                                    Optional<Player> playerOpt = plugin.getServer().getPlayer(member.getUUID());
                                    boolean isOnline = playerOpt.isPresent();
                                    return isOnline ?
                                            "<green>" + member.getName() :
                                            "<gray>" + member.getName();
                                })
                                .collect(Collectors.joining("<white>, </white>"));

                        // Send members in one line
                        source.reply("  " + memberList);
                        source.reply("");
                    }
                });
    }
}