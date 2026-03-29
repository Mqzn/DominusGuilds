package eg.mqzen.guilds.commands;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import eg.mqzen.guilds.DominusGuilds;
import studio.mevera.imperat.VelocityCommandSource;
import studio.mevera.imperat.command.arguments.Argument;
import studio.mevera.imperat.context.SuggestionContext;
import studio.mevera.imperat.providers.SuggestionProvider;

import java.util.List;
import java.util.stream.Collectors;

public class NonGuildMembersSuggestionProvider implements SuggestionProvider<VelocityCommandSource> {

    private final DominusGuilds plugin;
    private final ProxyServer server;
    public NonGuildMembersSuggestionProvider(DominusGuilds plugin, ProxyServer server)
    {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public List<String> provide(
            SuggestionContext<VelocityCommandSource> context,
            Argument<VelocityCommandSource> argument
    ) {

        if (context.source().isConsole()) return server.getAllPlayers().stream().map(Player::getUsername).toList();
        Player sourcePlayer = context.source().asPlayer();
        return server.getAllPlayers().stream()
                       .filter(p -> !p.getUniqueId().equals(sourcePlayer.getUniqueId()))
                       .filter(p -> plugin.getGuildManager().getPlayerGuild(p).isEmpty())
                       .map(Player::getUsername)
                       .collect(Collectors.toList());

    }
}
