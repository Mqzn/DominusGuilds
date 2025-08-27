package com.rivemc.guilds.commands;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.RiveGuilds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.imperat.VelocitySource;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.base.element.ParameterElement;
import studio.mevera.imperat.context.ExecutionContext;
import studio.mevera.imperat.exception.ImperatException;
import studio.mevera.imperat.exception.OnlyPlayerAllowedException;
import studio.mevera.imperat.resolvers.ContextResolver;

public class GuildContextResolver implements ContextResolver<VelocitySource, Guild<Player>> {

    private final RiveGuilds plugin;

    public GuildContextResolver(RiveGuilds plugin) {
        this.plugin = plugin;
    }


    @Override
    public @Nullable Guild<Player> resolve(
            @NotNull ExecutionContext<VelocitySource> context,
            @Nullable ParameterElement parameter
    ) throws ImperatException {
        if (context.source().isConsole()) {
            throw new OnlyPlayerAllowedException(context);
        }
        return plugin.getGuildManager().getPlayerGuild(context.source().asPlayer())
                .orElse(null);
    }
}
