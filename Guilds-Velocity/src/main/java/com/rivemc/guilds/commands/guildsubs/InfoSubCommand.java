package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildRole;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

@SubCommand("info")
@Description("Displays your guild info")
public class InfoSubCommand {
    
    @Dependency
    RiveGuilds plugin;

    @Usage
    public void info(VelocityPlayer source, @ContextResolved Guild<Player> sourceGuild) {
        // Traditional MiniMessage approach
        String message = buildMessage(source, sourceGuild);
        source.reply(message);
    }

    private String buildMessage(VelocityPlayer source, Guild<Player> sourceGuild) {
        GuildRole playerRole = sourceGuild.getMemberRole(source.uuid());

        // Format the foundation date
        String foundationDate = formatFoundationDate(sourceGuild.getFoundationDate());

        return "<gold>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n"
                + "<yellow><bold>                    ⚜ " + sourceGuild.getName().toUpperCase() + " ⚜</bold></yellow>\n"
                + "<gold>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n"
                + "<aqua>» <white><bold>Guild Information</bold></white>\n"
                + "<gray>  ├─ <gold>❂ Founded: <yellow>" + foundationDate + "</yellow></gold></gray>\n"
                + "<gray>  ├─ <green>Owner: <white>" + sourceGuild.getOwnerInfo().getName() + "</white></green></gray>\n"
                + "<gray>  ├─ <green>Tag: </green>" + sourceGuild.getTag().getMiniMessageFormattedTag()+ "]</gray>\n"
                + "<gray>  └─ <green>Members: <white>" + sourceGuild.getMembers().size() + "</white></green></gray>\n"

                + "<aqua>» <white><bold>Your Role</bold></white>\n"
                + "<gray>  ├─ <green>Name: <white>" + playerRole.getPrefix() + "</white></green></gray>\n"
                + "<gray>  └─ <green>Weight: <white>#" + playerRole.getWeight() + "</white></green></gray>\n"
                + "<gold>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";
    }

    /**
     * Formats the foundation date into a readable string with relative time
     */
    private String formatFoundationDate(java.util.Date foundationDate) {
        if (foundationDate == null) {
            return "Unknown";
        }

        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MMM dd, yyyy");
        String formattedDate = formatter.format(foundationDate);

        // Calculate days ago for additional context
        long daysDifference = (System.currentTimeMillis() - foundationDate.getTime()) / (1000 * 60 * 60 * 24);

        if (daysDifference == 0) {
            return formattedDate + " (Today!)";
        } else if (daysDifference == 1) {
            return formattedDate + " (Yesterday)";
        } else if (daysDifference < 30) {
            return formattedDate + " (" + daysDifference + " days ago)";
        } else if (daysDifference < 365) {
            long monthsAgo = daysDifference / 30;
            return formattedDate + " (" + monthsAgo + " month" + (monthsAgo > 1 ? "s" : "") + " ago)";
        } else {
            long yearsAgo = daysDifference / 365;
            return formattedDate + " (" + yearsAgo + " year" + (yearsAgo > 1 ? "s" : "") + " ago)";
        }
    }
}