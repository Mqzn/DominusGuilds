package com.rivemc.guilds.listeners;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMember;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.event.GuildChatEvent;
import com.rivemc.guilds.util.GuildMessageFormatter;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class ChatListener {

    private final RiveGuilds plugin;

    public ChatListener(RiveGuilds plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        Optional<Guild<Player>> sourceGuildOptional = plugin.getGuildManager().getPlayerGuild(player);
        if (sourceGuildOptional.isEmpty()) return;

        Guild<Player> sourceGuild = sourceGuildOptional.get();
        Optional<GuildMember<Player>> sourceMemberOptional = sourceGuild.getMember(player.getUniqueId());
        if (sourceMemberOptional.isEmpty()) {
            player.sendMessage(
                    GuildMessageFormatter.createErrorMessage("Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!")
            );
            player.sendMessage(
                    GuildMessageFormatter.createErrorMessage("Please contact an admin to resolve this matter.")
            );
            return;
        }

        GuildMember<Player> guildMember = sourceMemberOptional.get();
        if (!guildMember.hasChatToggled()) return;

        //cancelling the event to prevent it from being sent to the global chat
        event.setResult(PlayerChatEvent.ChatResult.denied());

        //we call the local guild chat event here
        GuildChatEvent guildChatEvent = new GuildChatEvent(sourceGuild, plugin.getConfig().getString("server"), player.getUniqueId(),
                event.getMessage());
        //we call the event, so other plugins can listen to it
        plugin.getServer().getEventManager().fire(guildChatEvent);

        //now we publish the message to proxy, so it sends this guild chat message to OTHER servers other than the one the player is currently in
        plugin.getGuildManager().publishGuildChatMessage(sourceGuild, player.getUniqueId(), event.getMessage());
    }

    @Subscribe(priority = 100)
    public void onGuildChat(GuildChatEvent event) {
        if(event.isCancelled()) {
            return;
        }

        Player player = plugin.getServer().getPlayer(event.getSenderId()).orElse(null);
        if(player == null) {
            //if the player is not online, we don't send the message
            return;
        }

        Optional<Guild<Player>> sourceGuildOptional = plugin.getGuildManager().getPlayerGuild(player);
        if (sourceGuildOptional.isEmpty()) return;

        Guild<Player> sourceGuild = sourceGuildOptional.get();
        Optional<GuildMember<Player>> sourceMemberOptional = sourceGuild.getMember(player.getUniqueId());
        if (sourceMemberOptional.isEmpty()) {
            return;
        }

        GuildMember<Player> guildMember = sourceMemberOptional.get();
        if (!guildMember.hasChatToggled()) return;
        
        // Create the formatted message using the message formatter utility
        Component formattedMessage = GuildMessageFormatter.formatGuildChatMessage(sourceGuild, player, event.getMessage());
        
        //now let's send the message to the guild members
        sourceGuild.getMembers().forEach(member ->
            plugin.getServer().getPlayer(member.getUUID())
                    .ifPresent(guildPlayer -> guildPlayer.sendMessage(formattedMessage))
        );

    }

}
