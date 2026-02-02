package eg.mqzen.guilds.commands;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.DominusGuilds;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.command.parameters.type.BaseParameterType;
import studio.mevera.imperat.context.ExecutionContext;
import studio.mevera.imperat.context.internal.CommandInputStream;
import studio.mevera.imperat.exception.ImperatException;
import studio.mevera.imperat.exception.SourceException;
import studio.mevera.imperat.resolvers.SuggestionResolver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuildMemberParameterType extends BaseParameterType<VelocitySource, GuildMember<Player>> {

    private final DominusGuilds plugin;

    public GuildMemberParameterType(DominusGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public GuildMember<Player> resolve(ExecutionContext<VelocitySource> ctx, CommandInputStream<VelocitySource> stream, String input) throws ImperatException {
        if (ctx.source().isConsole()) {
            throw new SourceException(ctx, "Only players can use this command");
        }

        Player player = ctx.source().asPlayer();
        
        Optional<Guild<Player>> guild = plugin.getGuildManager().getPlayerGuild(player);
        if (guild.isEmpty()) {
            throw new SourceException(ctx, "You are not in a guild");
        }

        return guild.get().getMemberByName(input)
                .orElseThrow(() -> new SourceException(ctx, "Player '" + input + "' is not in your guild"));
    }
    
    @Override
    public SuggestionResolver<VelocitySource> getSuggestionResolver() {
        return (ctx, param) -> {
            if(ctx.source().isConsole()) {
                return List.of();
            }
            return plugin.getGuildManager().getPlayerGuild(ctx.source().asPlayer())
                    .map(guild -> guild.getMembers().stream()
                            .map(GuildMember::getName)
                            .collect(Collectors.toList()))
                    .orElse(List.of());
        };
    }
}
