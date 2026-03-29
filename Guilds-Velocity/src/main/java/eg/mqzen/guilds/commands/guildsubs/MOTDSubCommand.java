package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMOTD;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.base.SimpleGuildMOTD;
import eg.mqzen.guilds.commands.VelocityPlayer;
import eg.mqzen.guilds.database.GuildUpdateAction;
import eg.mqzen.guilds.util.DurationParser;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.types.Context;
import studio.mevera.imperat.annotations.types.Default;
import studio.mevera.imperat.annotations.types.Dependency;
import studio.mevera.imperat.annotations.types.Flag;
import studio.mevera.imperat.annotations.types.Greedy;
import studio.mevera.imperat.annotations.types.Named;
import studio.mevera.imperat.annotations.types.SubCommand;
import studio.mevera.imperat.annotations.types.Execute;

import java.time.Duration;

@SubCommand("motd")
public class MOTDSubCommand {

    @Dependency
    DominusGuilds plugin;

    @Execute
    public void def(VelocityPlayer source) {
        source.reply("<gray>Please specify a message (MUST USE WITH QUOTATIONS)");
        source.reply("<green>Usage: <aqua>/guild motd <message> [duration]");
        source.reply("<dark_aqua>Example <yellow>#1<dark_aqua>: <gray>/guild motd \"Message Of the Day\"");
        source.reply("<dark_aqua>Example <yellow>#2<dark_aqua>: <gray>/guild motd -time 1h \"Message Of the Day\"");
    }

    @Execute
    public void mainUsage(
            VelocityPlayer source,
            @Context Guild<Player> sourceGuild,
            @Flag("time") @Default("24h") Duration time,
            @Named("message") @Greedy String message
    ) {
        Duration duration = time == null ? Duration.ofDays(1) : time;
        // /guild motd <message> [duration]
        //check if duration is less than 1 minute
        if(duration.isZero() || duration.isNegative() || duration.toMinutes() <= 0L) {
            source.reply("<gray>You have entered duration '<yellow>" + DurationParser.formatDuration(duration) + "</yellow><gray>'");
            source.reply("<red>Duration Must be atleast 1 minute");
            source.reply("<red>It is 24 hours by default");
            return;
        }

        GuildMOTD newMotd = new SimpleGuildMOTD(message,duration);
        sourceGuild.setMOTD(newMotd);
        plugin.getGuildManager().updateGuild(GuildUpdateAction.CHANGE_MOTD, sourceGuild.getID(), sourceGuild, false)
                .onSuccess((guild)-> {
                    source.reply("<gray>You have set the MOTD of your guild to '<reset>" + message + "</reset><gray>'");
                    source.reply("<gray>This MOTD will last for <green>" + DurationParser.formatDuration(duration));
                });
    }
}