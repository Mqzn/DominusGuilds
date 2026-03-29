package eg.mqzen.guilds.commands;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.DominusGuilds;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.imperat.VelocityCommandSource;
import studio.mevera.imperat.command.arguments.type.ArgumentType;
import studio.mevera.imperat.context.CommandContext;
import studio.mevera.imperat.exception.CommandException;
import studio.mevera.imperat.providers.SuggestionProvider;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuildMemberArgumentType extends ArgumentType<VelocityCommandSource, GuildMember<Player>> {

    private final DominusGuilds plugin;

    public GuildMemberArgumentType(DominusGuilds plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public SuggestionProvider<VelocityCommandSource> getSuggestionProvider() {
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

    @Override
    public GuildMember<Player> parse(
            @NotNull CommandContext<VelocityCommandSource> ctx,
            @NotNull String input
    ) throws CommandException {
        if (ctx.source().isConsole()) {
            throw new CommandException("Only players can use this command");
        }

        Player player = ctx.source().asPlayer();

        Optional<Guild<Player>> guild = plugin.getGuildManager().getPlayerGuild(player);
        if (guild.isEmpty()) {
            throw new CommandException("You are not in a guild");
        }

        return guild.get().getMemberByName(input)
                       .orElseThrow(() -> new CommandException("Player '" + input + "' is not in your guild"));
    }
}
