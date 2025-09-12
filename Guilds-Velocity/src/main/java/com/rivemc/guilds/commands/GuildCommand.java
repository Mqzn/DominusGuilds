package com.rivemc.guilds.commands;

import com.rivemc.guilds.GuildOwnerInfo;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.base.SimpleGuildOwnerInfo;
import com.rivemc.guilds.commands.guildsubs.AllySubCommand;
import com.rivemc.guilds.commands.guildsubs.ChatSubCommand;
import com.rivemc.guilds.commands.guildsubs.DemoteSubCommand;
import com.rivemc.guilds.commands.guildsubs.DenyInviteSubCommand;
import com.rivemc.guilds.commands.guildsubs.EnemySubCommand;
import com.rivemc.guilds.commands.guildsubs.FindSubCommand;
import com.rivemc.guilds.commands.guildsubs.InfoSubCommand;
import com.rivemc.guilds.commands.guildsubs.InviteSubCommand;
import com.rivemc.guilds.commands.guildsubs.JoinGuildSubCommand;
import com.rivemc.guilds.commands.guildsubs.KickSubCommand;
import com.rivemc.guilds.commands.guildsubs.LeaveSubCommand;
import com.rivemc.guilds.commands.guildsubs.ListSubCommand;
import com.rivemc.guilds.commands.guildsubs.MOTDSubCommand;
import com.rivemc.guilds.commands.guildsubs.PermissionsSubCommand;
import com.rivemc.guilds.commands.guildsubs.PromoteSubCommand;
import com.rivemc.guilds.commands.guildsubs.RenameSubCommand;
import com.rivemc.guilds.commands.guildsubs.SetRoleSubCommand;
import com.rivemc.guilds.commands.guildsubs.TagColorSubCommand;
import com.rivemc.guilds.commands.guildsubs.TagSubCommand;
import com.rivemc.guilds.commands.guildsubs.ToggleSubCommand;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.annotations.Command;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.ExternalSubCommand;
import studio.mevera.imperat.annotations.Named;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;
import studio.mevera.imperat.command.tree.help.CommandHelp;
import studio.mevera.imperat.command.tree.help.HelpFilters;
import studio.mevera.imperat.command.tree.help.HelpQuery;

@Command(value = {"guild", "g", "clan", "c"})
@Description("Guild management commands")
@ExternalSubCommand(
        {
                MOTDSubCommand.class,
                ChatSubCommand.class,
                FindSubCommand.class,
                InfoSubCommand.class,
                PermissionsSubCommand.class,
                SetRoleSubCommand.class,
                TagSubCommand.class,
                TagColorSubCommand.class,
                PromoteSubCommand.class,
                DemoteSubCommand.class,
                ListSubCommand.class,
                InviteSubCommand.class,
                JoinGuildSubCommand.class,
                DenyInviteSubCommand.class,
                ToggleSubCommand.class,
                RenameSubCommand.class,
                LeaveSubCommand.class,
                KickSubCommand.class,
                AllySubCommand.class,
                EnemySubCommand.class,
        }
)
public class GuildCommand {

    private final RiveGuilds plugin;
    
    public GuildCommand(RiveGuilds plugin) {
        this.plugin = plugin;
    }

    @SubCommand("create")
    @Description("Create a new guild")
    public void createGuild(VelocitySource source, @Named("name") String guildName) {
        if(source.isConsole()) {
            source.error("Only players can do this");
            return;
        }
        Player player = source.asPlayer();
        if (plugin.getGuildManager().getPlayerGuild(player).isPresent()) {
            source.error("You are already in a guild");
            return;
        }

        if (plugin.getGuildManager().getGuildByName(guildName).isPresent()) {
            source.error("A guild with that name already exists");
            return;
        }

        GuildOwnerInfo ownerInfo = new SimpleGuildOwnerInfo(player.getUsername(), player.getUniqueId());

        plugin.getGuildManager().createGuild(guildName, ownerInfo)
                .onSuccess(guild -> source.reply("<green>Successfully created guild '" + guildName + "'"))
                .onError(error -> source.error("Failed to create guild: " + error.getMessage()));
    }
    
    @Usage
    
    @Description("Show guild information or help")
    public void defaultCommand(VelocitySource source, CommandHelp<VelocitySource> help) {
        help.display(
                HelpQuery.<VelocitySource>builder()
                        .filter(HelpFilters.hasPermission(source, help))
                        .build(),
                
                new GuildHelpTheme()
        );
    }
}
