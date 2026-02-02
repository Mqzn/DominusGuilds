package eg.mqzen.guilds.commands;

import eg.mqzen.guilds.GuildOwnerInfo;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.base.SimpleGuildOwnerInfo;
import eg.mqzen.guilds.commands.guildsubs.AllySubCommand;
import eg.mqzen.guilds.commands.guildsubs.ChatSubCommand;
import eg.mqzen.guilds.commands.guildsubs.DemoteSubCommand;
import eg.mqzen.guilds.commands.guildsubs.DenyInviteSubCommand;
import eg.mqzen.guilds.commands.guildsubs.EnemySubCommand;
import eg.mqzen.guilds.commands.guildsubs.FindSubCommand;
import eg.mqzen.guilds.commands.guildsubs.InfoSubCommand;
import eg.mqzen.guilds.commands.guildsubs.InviteSubCommand;
import eg.mqzen.guilds.commands.guildsubs.JoinGuildSubCommand;
import eg.mqzen.guilds.commands.guildsubs.KickSubCommand;
import eg.mqzen.guilds.commands.guildsubs.LeaveSubCommand;
import eg.mqzen.guilds.commands.guildsubs.ListSubCommand;
import eg.mqzen.guilds.commands.guildsubs.MOTDSubCommand;
import eg.mqzen.guilds.commands.guildsubs.PermissionsSubCommand;
import eg.mqzen.guilds.commands.guildsubs.PromoteSubCommand;
import eg.mqzen.guilds.commands.guildsubs.RenameSubCommand;
import eg.mqzen.guilds.commands.guildsubs.SetRoleSubCommand;
import eg.mqzen.guilds.commands.guildsubs.TagColorSubCommand;
import eg.mqzen.guilds.commands.guildsubs.TagSubCommand;
import eg.mqzen.guilds.commands.guildsubs.ToggleSubCommand;
import eg.mqzen.guilds.commands.guildsubs.DisbandSubCommand;
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
                DisbandSubCommand.class,
        }
)
public class GuildCommand {

    private final DominusGuilds plugin;
    
    public GuildCommand(DominusGuilds plugin) {
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
