package com.rivemc.guilds.commands;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildOwnerInfo;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.commands.guildsubs.*;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.annotations.*;
import com.rivemc.guilds.base.SimpleGuildOwnerInfo;
import com.velocitypowered.api.proxy.Player;

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
                LeaveSubCommand.class
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

    @Description("Show guild information or help")
    @Usage
    public void defaultCommand(VelocitySource source, @Optional Guild<Player> guild) {
        if (guild != null) {
            // Show guild info if player is in a guild
            source.reply("Guild: " + guild.getName());
            source.reply("Members: " + guild.getMembers().size());
        } else {
            // Show help if not in a guild
            source.reply("Use /guild create <name> to create a guild or /guild help for more commands");
        }
    }
}
