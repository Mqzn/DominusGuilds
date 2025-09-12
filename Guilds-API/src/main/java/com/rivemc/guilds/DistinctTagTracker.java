package com.rivemc.guilds;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Tracks distinct tags associated with guilds, ensuring uniqueness and providing
 * methods to add, remove, update, and check the availability of tags.
 */
public interface DistinctTagTracker {

    /**
     * Retrieves the unique identifier of a guild associated with a given tag.
     *
     * @param tag The tag to look up.
     * @return The UUID of the guild associated with the tag, or null if not found.
     */
    Optional<UUID> getGuildIdByTag(@NotNull String tag);

    /**
     * Adds a distinct tag for a guild, using the guild's current tag's value and the guild's unique identifier.
     * @see Guild#getTag()
     * @see GuildTag#getPlainValue()
     *
     * @param guild The guild from which to derive the tag.
     */
    void addTagFrom(@NotNull Guild<?> guild);

    /**
     * Adds a distinct tag for a guild.
     *
     * @param tag     The tag to add.
     * @param guildId The unique identifier of the guild associated with the tag.
     */
    void addTag(@NotNull String tag, @NotNull UUID guildId);

    /**
     * Removes a distinct tag associated with a guild.
     *
     * @param tag     The tag to remove.
     * @param guildId The unique identifier of the guild associated with the tag.
     */
    void removeTag(@NotNull String tag, @NotNull UUID guildId);

    /**
     * Updates a distinct tag for a guild, replacing the old tag with a new one.
     *
     * @param oldTag  The existing tag to be replaced.
     * @param newTag  The new tag to associate with the guild.
     * @param guildId The unique identifier of the guild associated with the tag.
     */
    void updateTag(@NotNull String oldTag, @NotNull String newTag, @NotNull UUID guildId);

    /**
     * Checks if a tag is already taken by any guild.
     *
     * @param tag The tag to check for availability.
     * @return True if the tag is taken, false otherwise.
     */
    boolean isTaken(@NotNull String tag);
}
