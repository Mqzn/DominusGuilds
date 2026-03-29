package eg.mqzen.guilds.commands;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.GuildRole;
import eg.mqzen.guilds.DominusGuilds;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.imperat.VelocityCommandSource;
import studio.mevera.imperat.annotations.base.element.ParameterElement;
import studio.mevera.imperat.context.ExecutionContext;
import studio.mevera.imperat.exception.CommandException;
import studio.mevera.imperat.exception.ResponseException;
import studio.mevera.imperat.providers.ContextArgumentProvider;

public class GuildContextResolver implements ContextArgumentProvider<VelocityCommandSource, Guild<Player>> {

    private final DominusGuilds plugin;

    public GuildContextResolver(DominusGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public @org.jspecify.annotations.Nullable Guild<Player> provide(
            @NotNull ExecutionContext<VelocityCommandSource> context,
            @Nullable ParameterElement parameter
    ) throws CommandException {
        if (context.source().isConsole()) {
            throw new CommandException("<red>Only players can execute this command");
        }

        if (parameter != null && parameter.getOwningClass().isAnnotationPresent(RequiredGuildPermissions.class)) {
            RequiredGuildPermissions annotation = parameter.getOwningClass().getAnnotation(RequiredGuildPermissions.class);
            assert annotation != null;
            GuildRole.Permission[] permissions = annotation.value();
            Guild<Player> playerGuild = plugin.getGuildManager().getPlayerGuild(context.source().asPlayer())
                                                .orElseThrow(() -> new NotInGuildException());

            GuildRole role = playerGuild.getMember(context.source().asPlayer().getUniqueId())
                                     .map(GuildMember::getRoleId)
                                     .flatMap(playerGuild::getRoleByID)
                                     .orElseThrow(() -> new IllegalStateException("Unexpectedly missing role for guild member"));

            for (GuildRole.Permission permission : permissions) {
                if (!role.hasPermission(permission)) {
                    throw new InsufficientGuildPermissionException();
                }
            }
            return playerGuild;
        }

        return plugin.getGuildManager().getPlayerGuild(context.source().asPlayer())
                       .orElseThrow(() -> new NotInGuildException());
    }
}
