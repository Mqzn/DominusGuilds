package eg.mqzen.guilds;

import eg.mqzen.guilds.commands.DurationArgumentType;
import eg.mqzen.guilds.commands.GuildCommand;
import eg.mqzen.guilds.commands.GuildContextResolver;
import eg.mqzen.guilds.commands.GuildMemberArgumentType;
import eg.mqzen.guilds.commands.GuildQueryResult;
import eg.mqzen.guilds.commands.GuildQueryResultArgumentType;
import eg.mqzen.guilds.commands.NonGuildMembersSuggestionProvider;
import eg.mqzen.guilds.commands.RequiredGuildPermissions;
import eg.mqzen.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import studio.mevera.imperat.VelocityImperat;
import studio.mevera.imperat.util.TypeWrap;

import java.time.Duration;

public class CommandRegistrar {
    
    private final VelocityImperat<DominusGuilds> imperat;
    private final Logger logger;
    
    public CommandRegistrar(DominusGuilds plugin, ProxyServer server, Logger logger) {
        this.logger = logger;
        imperat = VelocityImperat.builder(plugin, server)
                //.helpProvider(new GuildsHelpProvider())
                .sourceProvider(VelocityPlayer.class, (src, ctx)-> {
                    return new VelocityPlayer(src);
                })
                .argType(new TypeWrap<GuildMember<Player>>(){}.getType(), new GuildMemberArgumentType(plugin))
                .argType(Duration.class, new DurationArgumentType())
                .argType(GuildQueryResult.class, new GuildQueryResultArgumentType(plugin))
                .contextArgumentProvider(new TypeWrap<Guild<Player>>(){}.getType(), new GuildContextResolver(plugin))
                .dependencyResolver(DominusGuilds.class, () -> plugin)
                .dependencyResolver(GuildCommand.class, ()-> new GuildCommand(plugin))
                .dependencyResolver(NonGuildMembersSuggestionProvider.class, ()-> new NonGuildMembersSuggestionProvider(plugin, server))
                .build();
    }

    public void registerCommands() {
        //TODO register commands here
        imperat.registerAnnotations(RequiredGuildPermissions.class);
        imperat.registerCommand(GuildCommand.class);
        logger.info("Registered guild commands");
    }
}
