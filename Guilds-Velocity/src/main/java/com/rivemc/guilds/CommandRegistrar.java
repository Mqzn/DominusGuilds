package com.rivemc.guilds;

import com.rivemc.guilds.commands.*;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.VelocityImperat;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import studio.mevera.imperat.util.TypeWrap;

import java.time.Duration;
import java.util.stream.Collectors;

public class CommandRegistrar {
    
    private final VelocityImperat imperat;
    private final Logger logger;
    
    public CommandRegistrar(RiveGuilds plugin, ProxyServer server, Logger logger) {
        this.logger = logger;
        imperat = VelocityImperat.builder(server.getPluginManager().fromInstance(plugin).orElseThrow(), server)
                //.helpProvider(new GuildsHelpProvider())
                .sourceResolver(VelocityPlayer.class, VelocityPlayer::new)
                .parameterType(new TypeWrap<GuildMember<Player>>(){}.getType(), new GuildMemberParameterType(plugin))
                .parameterType(Duration.class, new DurationParameterType())
                .contextResolver(new TypeWrap<Guild<Player>>(){}.getType(), new GuildContextResolver(plugin))
                .dependencyResolver(RiveGuilds.class, () -> plugin)
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
        imperat.registerCommand(GuildCommand.class);
        logger.info("Registered guild commands");
    }
}
