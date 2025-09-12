package com.rivemc.guilds.commands;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMember;
import com.rivemc.guilds.GuildRole;
import com.rivemc.guilds.RiveGuilds;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.imperat.VelocitySource;
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

        if (parameter != null && parameter.getOwningClass().isAnnotationPresent(RequiredGuildPermissions.class)) {
            RequiredGuildPermissions annotation = parameter.getOwningClass().getAnnotation(RequiredGuildPermissions.class);
            assert annotation != null;
            GuildRole.Permission[] permissions = annotation.value();
            Guild<Player> playerGuild = plugin.getGuildManager().getPlayerGuild(context.source().asPlayer())
                    .orElseThrow(() -> new NotInGuildException(context));

            GuildRole role = playerGuild.getMember(context.source().asPlayer().getUniqueId())
                    .map(GuildMember::getRoleId)
                    .flatMap(playerGuild::getRoleByID)
                    .orElseThrow(() -> new IllegalStateException("Unexpectedly missing role for guild member"));

            for (GuildRole.Permission permission : permissions) {
                if (!role.hasPermission(permission)) {
                    throw new InsufficientGuildPermissionException(context);
                }
            }
            return playerGuild;
        }

        return plugin.getGuildManager().getPlayerGuild(context.source().asPlayer())
                .orElseThrow(() -> new NotInGuildException(context));
    }
}
