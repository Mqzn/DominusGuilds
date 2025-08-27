package com.rivemc.guilds.listeners;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMOTD;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.database.GuildUpdateAction;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.time.Instant;

/**
 * @author Mqzen
 * @date 18/5/2025
 */
public final class RegistryListener {

    private final RiveGuilds plugin;

    public RegistryListener(RiveGuilds plugin) {
        this.plugin = plugin;
    }

    @Subscribe()
    public void onJoin(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        // Join messages shouldn't be canceled in this plugin.
        //event.setJoinMessage(null);
        
        plugin.getGuildManager()
                .getPlayerGuild(player).ifPresent((sourceGuild)-> {
                    Component joinMessage = sourceGuild.getTag().getAdventureFormattedTag().append(Component.space())
                            .append(Component.text("+")).append(Component.space())
                            .append(Component.text(player.getUsername(), NamedTextColor.GRAY));
                    sourceGuild.broadcast(plugin.getGuildManager(), joinMessage);
                    handleGuildMOTD(sourceGuild, player);
                });
    }

    private void handleGuildMOTD(Guild<Player> sourceGuild, Player player) {
        if(sourceGuild.getMOTD() == null) return;
        GuildMOTD motd = sourceGuild.getMOTD();
        Instant expirationTime = Instant.now().plus(motd.getExpiryDuration());

        // Later, check if expired
        if (Instant.now().isAfter(expirationTime)) {
            plugin.log("Expired MOTD for guild '" + sourceGuild.getName() + "'");
            sourceGuild.resetMOTD();
            plugin.getGuildManager().updateGuild(GuildUpdateAction.RESET_MOTD, sourceGuild.getID(), sourceGuild, false);
        }else {
            //send to the joining player using fancy MiniMessage format
            player.sendRichMessage("<gray><strikethrough>====================</strikethrough></gray>");
            player.sendRichMessage("<gray>           </gray><green><bold>📜 Guild's MOTD 📜</bold></green>");
            player.sendRichMessage("<yellow>" + motd.getValue() + "</yellow>");
            player.sendRichMessage("<gray><strikethrough>====================</strikethrough></gray>");
        }

    }

}
