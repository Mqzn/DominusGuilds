package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.commands.NonGuildMembersSuggestionProvider;
import eg.mqzen.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.types.Context;
import studio.mevera.imperat.annotations.types.Dependency;
import studio.mevera.imperat.annotations.types.Description;
import studio.mevera.imperat.annotations.types.Named;
import studio.mevera.imperat.annotations.types.SubCommand;
import studio.mevera.imperat.annotations.types.Execute;
import studio.mevera.imperat.annotations.types.SuggestionProvider;

import java.util.Optional;

@SubCommand("invite")
@Description("Invite a player to your guild")
public class InviteSubCommand {

    @Dependency
    DominusGuilds plugin;

    @Execute
    public void defaultUsage(VelocityPlayer source) {
        source.reply("Usage: /guild invite <player>");
    }

    @Execute
    public void inviteToOwnGuild(
            VelocityPlayer source,
            @Context Guild<Player> sourceGuild,
            @Named("target")
            @SuggestionProvider(NonGuildMembersSuggestionProvider.class) String target
    ) {
        // Check if the target player is online on the proxy
        Optional<Player> playerOpt = plugin.getServer().getPlayer(target);
        playerOpt.ifPresent(player ->
                plugin.getGuildManager().invitePlayerToGuild(
                    source.asPlayer(), sourceGuild, player
                )
        );
    }
}