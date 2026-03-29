package eg.mqzen.guilds.commands;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.DominusGuilds;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.imperat.VelocityCommandSource;
import studio.mevera.imperat.command.arguments.type.ArgumentType;
import studio.mevera.imperat.context.CommandContext;
import studio.mevera.imperat.exception.CommandException;


public class GuildQueryResultArgumentType extends ArgumentType<VelocityCommandSource, GuildQueryResult> {

    private final DominusGuilds plugin;
    public GuildQueryResultArgumentType(DominusGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public GuildQueryResult parse(
            @NotNull CommandContext<VelocityCommandSource> context,
            @NotNull String input
    ) throws CommandException {
        //check if its player name and find guild from the player's guild if its a player
        Player player = plugin.getServer().getPlayer(input).orElse(null);
        if(player != null) {
            return plugin.getGuildManager().getPlayerGuild(player)
                           .map((guild)-> new GuildQueryResult(plugin, input, guild.getID(), GuildQueryResult.Type.PLAYER_NAME))
                           .orElseThrow(()-> new TargetNotInAnyGuildException(input));

        }

        //then check if its a guild name
        Guild<Player> guild = plugin.getGuildManager().getGuildByName(input).orElse(null);
        if(guild != null) {
            return new GuildQueryResult(plugin, input, guild.getID(), GuildQueryResult.Type.GUILD_NAME);
        }

        //finally check if its a guild tag
        return plugin.getGuildManager().getDistinctTagTracker().getGuildIdByTag(input)
                       .map((guildId) -> new GuildQueryResult(plugin, input, guildId, GuildQueryResult.Type.GUILD_TAG))
                       .orElseThrow(()-> new UnknownGuildTagException(input));
    }
}
