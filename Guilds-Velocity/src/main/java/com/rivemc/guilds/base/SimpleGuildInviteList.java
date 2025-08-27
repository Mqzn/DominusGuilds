package com.rivemc.guilds.base;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildInviteList;
import com.rivemc.guilds.RiveGuilds;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SimpleGuildInviteList implements GuildInviteList<Player> {

    private final Guild<Player> guild;
    private final RiveGuilds plugin;

    private final Cache<UUID, UUID> invitesCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<UUID, UUID>() {
                @Override
                public void onRemoval(UUID key, UUID value, @NotNull RemovalCause cause) {
                    if(cause == RemovalCause.EXPIRED) {
                        // Handle expired invites if needed
                        //send message to player that it expired
                        //System.out.println("Invite for player " + key + " expired.");
                        plugin.getServer().getPlayer(key)
                            .ifPresent(player -> player.sendRichMessage("Your invite to guild '" + guild.getName() + "' has expired."));
                    }
                }
            })
            .build();

    public SimpleGuildInviteList(Guild<Player> guild, RiveGuilds plugin) {
        this.guild = guild;
        this.plugin = plugin;
    }

    /**
     * Gets the guild associated with this invite list.
     *
     * @return The guild associated with this invite list.
     */
    @Override
    public @NotNull Guild<Player> getGuild() {
        return guild;
    }

    /**
     * Gets the UUID of the player who invited a specific player to the guild.
     *
     * @param invited The UUID of the invited player.
     * @return The UUID of the inviter, or null if not found.
     */
    @Override
    public @Nullable Optional<UUID> getInviter(UUID invited) {
        return Optional.ofNullable(invitesCache.getIfPresent(invited));
    }

    /**
     * Gets the set of players who are invited to the guild by a specific inviter.
     * This method retrieves the list of players who have been invited by the specified inviter.
     *
     * @param inviterId The UUID of the inviter.
     * @return a set of UUIDs representing the invited players.
     */
    @Override
    public @NotNull Set<UUID> getInvitedPlayers(UUID inviterId) {
        return invitesCache.asMap().entrySet().stream()
                .filter(entry -> entry.getValue().equals(inviterId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Adds a player to the invite list of a guild.
     *
     * @param inviterId the UUID of the inviter.
     * @param targetId    The player to invite.
     */
    @Override
    public void addPlayer(UUID inviterId, UUID targetId) {
        invitesCache.put(targetId, inviterId);
    }

    /**
     * Removes a player from the invite list of a guild.
     *
     * @param playerId The name of the player to remove his invite.
     */
    @Override
    public void removeInvite(UUID playerId) {
        invitesCache.asMap().remove(playerId);
    }

    /**
     * Checks if a player is in the invite list of a guild.
     *
     * @param playerId The player to check.
     * @return true if the player is in the invite list, false otherwise.
     */
    @Override
    public boolean isInvited(UUID playerId) {
        return invitesCache.asMap().get(playerId) != null;
    }

}
