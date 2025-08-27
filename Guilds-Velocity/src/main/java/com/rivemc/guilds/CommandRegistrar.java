package com.rivemc.guilds;

import com.rivemc.guilds.commands.*;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.VelocityImperat;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import studio.mevera.imperat.util.TypeWrap;

import java.time.Duration;

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
                .contextResolver(Guild.class, new GuildContextResolver(plugin))
                .dependencyResolver(RiveGuilds.class, () -> plugin)
                .build();
    }

    public void registerCommands() {
        //TODO register commands here
        imperat.registerCommand(GuildCommand.class);
        logger.info("Registered guild commands");
    }
}
