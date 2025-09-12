package com.rivemc.guilds;

import com.rivemc.guilds.database.GuildStorage;
import com.rivemc.guilds.database.GuildUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Manages guild operations, providing methods to create, delete, retrieve,
 * and update guilds. It also handles player invitations and chat message publishing.
 */
public interface GuildManager<P> {

    /**
     * Retrieves a guild by its name.
     *
     * @param guildName The name of the guild.
     * @return An Optional containing the guild if found, or an empty Optional if not.
     */
    @NotNull Optional<Guild<P>> getGuildByName(@NotNull String guildName);

    /**
     * Retrieves a guild by its unique ID.
     *
     * @param guildID The unique ID of the guild.
     * @return An Optional containing the guild if found, or an empty Optional if not.
     */
    @NotNull Optional<Guild<P>> getGuildByID(@NotNull UUID guildID);

    /**
     * Creates a new guild with the specified name and owner information.
     *
     * @param guildName The name of the new guild.
     * @param owner The owner information for the guild.
     * @return A CompletableFuture that completes with the created Guild object.
     */
    @NotNull FutureOperation<Guild<P>> createGuild(@NotNull String guildName, @NotNull GuildOwnerInfo owner);

    /**
     * Deletes a guild by its name.
     *
     * @param guildName The name of the guild to delete.
     */
    default void deleteGuild(@NotNull String guildName) {
        Optional<Guild<P>> guildOptional = getGuildByName(guildName);
        guildOptional.ifPresent(this::deleteGuild);
    }

    /**
     * Deletes a guild by its unique ID.
     *
     * @param guildID The unique ID of the guild to delete.
     */
    default void deleteGuild(@NotNull UUID guildID) {
        Optional<Guild<P>> guildOptional = getGuildByID(guildID);
        guildOptional.ifPresent(this::deleteGuild);
    }

    /**
     * Deletes a guild.
     *
     * @param guild The guild to delete.
     * @return A CompletableFuture that completes when the guild is deleted.
     */
    FutureOperation<Void> deleteGuild(@NotNull Guild<P> guild);

    /**
     * Retrieves the guild a player is a member of, by the player's UUID.
     *
     * @param playerUUID The unique ID of the player.
     * @return An Optional containing the guild if the player is in one, or an empty Optional if not.
     */
    @NotNull Optional<Guild<P>> getPlayerGuild(@NotNull UUID playerUUID);

    /**
     * Retrieves the guild a player is a member of, by the player's name.
     *
     * @param playerName The name of the player.
     * @return An Optional containing the guild if the player is in one, or an empty Optional if not.
     */
    @NotNull Optional<Guild<P>> getPlayerGuild(@NotNull String playerName);

    /**
     * Retrieves the guild a player is a member of.
     *
     * @param player The player.
     * @return An Optional containing the guild if the player is in one, or an empty Optional if not.
     */
    @NotNull Optional<Guild<P>> getPlayerGuild(P player);

    /**
     * Gets the guild storage implementation.
     *
     * @return The GuildStorage instance used by this manager.
     */
    @NotNull GuildStorage<P> getStorage();

    /**
     * Gets the distinct tag tracker.
     *
     * @return The DistinctTagTracker instance used by this manager.
     */
    @NotNull DistinctTagTracker getDistinctTagTracker();

    /**
     * Updates a guild's information.
     *
     * @param updateAction The action that triggered the update.
     * @param id The unique ID of the guild to update.
     * @param newGuild The new Guild object with updated information.
     * @param databaseUpdate Whether to update the database with the new information.
     * @return A CompletableFuture that completes with the updated Guild object.
     */
    FutureOperation<Guild<P>> updateGuild(@NotNull GuildUpdateAction updateAction, @NotNull UUID id, Guild<P> newGuild, boolean databaseUpdate);

    /**
     * Updates a guild's information without specifying an update action.
     *
     * @param id The unique ID of the guild to update.
     * @param newGuild The new Guild object with updated information.
     * @param databaseUpdate Whether to update the database with the new information.
     * @return A CompletableFuture that completes with the updated Guild object.
     */
    default FutureOperation<Guild<P>> updateGuild(@NotNull UUID id, Guild<P> newGuild, boolean databaseUpdate) {
        return updateGuild(GuildUpdateAction.UNKNOWN, id, newGuild, databaseUpdate);
    }

    /**
     * Publishes a chat message to the guild's chat channel.
     *
     * @param guild The guild to publish the message to.
     * @param senderId The unique ID of the message sender.
     * @param message The chat message to publish.
     */
    void publishGuildChatMessage(@NotNull Guild<P> guild, @NotNull UUID senderId, @NotNull String message);

    /**
     * Publishes a broadcast message to all members of the specified guild.
     * <p>
     * This method is used to send an announcement or notification to every member
     * of the given guild, regardless of their online status or location. The message
     * is typically used for important updates, alerts, or system-wide notifications
     * relevant to the entire guild.
     * </p>
     *
     * @param guild   The guild to which the broadcast message will be sent.
     * @param message The message content to broadcast to all guild members.
     */
    void publishGuildBroadcastMessage(@NotNull Guild<P> guild, @NotNull String message);

    /**
     * Publishes a guild invite to the proxy server.
     * <p>
     * This method is used to send a guild invitation to a player who is not currently online
     * on the same server. It utilizes the `guildRedisManager` to publish the invite information
     * to the proxy, which can then forward the invitation to the appropriate server where the
     * target player is located.
     *
     * @param inviter    The player who is sending the invitation.
     * @param targetName The UUID of the player being invited.
     * @param guild      The guild to which the player is being invited.
     */
    void publishInviteToProxy(@NotNull P inviter, @NotNull String targetName, @NotNull Guild<P> guild);

    /**
     * Invites a target player to the source player's guild.
     * <p>
     * This method checks if the target player is already in a guild, verifies the inviter's permissions,
     * and ensures the target has not already been invited. If all conditions are met, the target player
     * is added to the guild's invite list and notified of the invitation.
     * </p>
     *
     * @param source      The player sending the invitation.
     * @param sourceGuild The guild the source player belongs to and is inviting the target to join.
     * @param target  The player being invited to the guild.
     */
    void invitePlayerToGuild(P source, @NotNull Guild<P> sourceGuild, @NotNull P target);

    //void publishGuildPlayerJoinNetworkNotification(@NotNull Guild guild, @NotNull UUID joiningPlayerId);
}
