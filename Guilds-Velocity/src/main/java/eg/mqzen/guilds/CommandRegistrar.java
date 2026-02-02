package eg.mqzen.guilds;

import eg.mqzen.guilds.commands.DurationParameterType;
import eg.mqzen.guilds.commands.GuildCommand;
import eg.mqzen.guilds.commands.GuildContextResolver;
import eg.mqzen.guilds.commands.GuildMemberParameterType;
import eg.mqzen.guilds.commands.GuildQueryResult;
import eg.mqzen.guilds.commands.GuildQueryResultParamType;
import eg.mqzen.guilds.commands.RequiredGuildPermissions;
import eg.mqzen.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import studio.mevera.imperat.VelocityImperat;
import studio.mevera.imperat.util.TypeWrap;

import java.time.Duration;
import java.util.stream.Collectors;

public class CommandRegistrar {
    
    private final VelocityImperat<DominusGuilds> imperat;
    private final Logger logger;
    
    public CommandRegistrar(DominusGuilds plugin, ProxyServer server, Logger logger) {
        this.logger = logger;
        imperat = VelocityImperat.builder(plugin, server)
                //.helpProvider(new GuildsHelpProvider())
                .sourceResolver(VelocityPlayer.class, VelocityPlayer::new)
                .parameterType(new TypeWrap<GuildMember<Player>>(){}.getType(), new GuildMemberParameterType(plugin))
                .parameterType(Duration.class, new DurationParameterType())
                .parameterType(GuildQueryResult.class, new GuildQueryResultParamType(plugin))
                .contextResolver(new TypeWrap<Guild<Player>>(){}.getType(), new GuildContextResolver(plugin))
                .dependencyResolver(DominusGuilds.class, () -> plugin)
                .dependencyResolver(GuildCommand.class, ()-> new GuildCommand(plugin))
                .namedSuggestionResolver("non-guild-players", (context, name) -> {
                    if (context.source().isConsole()) return server.getAllPlayers().stream().map(Player::getUsername).toList();
                    Player sourcePlayer = context.source().asPlayer();
                    return server.getAllPlayers().stream()
                            .filter(p -> !p.getUniqueId().equals(sourcePlayer.getUniqueId()))
                            .filter(p -> plugin.getGuildManager().getPlayerGuild(p).isEmpty())
                            .map(Player::getUsername)
                            .collect(Collectors.toList());
                })
                .build();
    }

    public void registerCommands() {
        //TODO register commands here
        imperat.registerAnnotations(RequiredGuildPermissions.class);
        imperat.registerCommand(GuildCommand.class);
        logger.info("Registered guild commands");
    }
}
