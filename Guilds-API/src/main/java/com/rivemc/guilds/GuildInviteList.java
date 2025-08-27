package com.rivemc.guilds;

import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a list of players invited to a guild.
 * This interface provides methods to manage and query the invite list for a specific guild.
 */
public interface GuildInviteList<P> {

    /**
     * Gets the guild associated with this invite list.
     *
     * @return The guild associated with this invite list.
     */
    @NotNull Guild<P> getGuild();

    /**
     * Gets the UUID of the player who invited a specific player to the guild.
     *
     * @param invited The UUID of the invited player.
     * @return The UUID of the inviter, or null if not found.
     */
    Optional<UUID> getInviter(UUID invited);

    /**
     * Gets the set of players who are invited to the guild by a specific inviter.
     * This method retrieves the list of players who have been invited by the specified inviter.
     *
     * @param inviterId The UUID of the inviter.
     * @return a set of UUIDs representing the invited players.
     */
    @NotNull Set<UUID> getInvitedPlayers(UUID inviterId);

    /**
     * Adds a player to the invite list of a guild.
     *
     * @param inviterId the UUID of the inviter.
     * @param player    The player to invite.
     */
    void addPlayer(UUID inviterId, UUID player);

    /**
     * Removes a player from the invite list of a guild.
     *
     * @param playerId  The name of the player to remove his invite.
     */
    void removeInvite(UUID playerId);

    /**
     * Checks if a player is in the invite list of a guild.
     *
     * @param playerId The player to check.
     * @return true if the player is in the invite list, false otherwise.
     */
    boolean isInvited(UUID playerId);

}
