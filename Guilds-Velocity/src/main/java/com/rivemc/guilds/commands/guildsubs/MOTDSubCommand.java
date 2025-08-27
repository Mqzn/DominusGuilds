package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.commands.VelocityPlayer;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Default;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Flag;
import studio.mevera.imperat.annotations.Greedy;
import studio.mevera.imperat.annotations.Named;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;
import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMOTD;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.base.SimpleGuildMOTD;
import com.rivemc.guilds.database.GuildUpdateAction;
import com.rivemc.guilds.util.DurationParser;
import com.velocitypowered.api.proxy.Player;

import java.time.Duration;

@SubCommand("motd")
public class MOTDSubCommand {

    @Dependency
    RiveGuilds plugin;

    @Usage
    public void def(VelocityPlayer source) {
        source.reply("<gray>Please specify a message (MUST USE WITH QUOTATIONS)");
        source.reply("<green>Usage: <aqua>/guild motd <message> [duration]");
        source.reply("<dark_aqua>Example <yellow>#1<dark_aqua>: <gray>/guild motd \"Message Of the Day\"");
        source.reply("<dark_aqua>Example <yellow>#2<dark_aqua>: <gray>/guild motd -time 1h \"Message Of the Day\"");
    }

    @Usage
    public void mainUsage(
            VelocityPlayer source,
            @ContextResolved Guild<Player> sourceGuild,
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