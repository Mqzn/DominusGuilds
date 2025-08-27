package com.rivemc.guilds;

import org.jetbrains.annotations.NotNull;
import java.util.UUID;

/**
 * Represents a member of a guild.
 * A guild member has a name, unique identifier, and a role within the guild.
 */
public interface GuildMember<P> {

    /**
     * Gets the name of the guild member.
     *
     * @return the name of the member, never null.
     */
    @NotNull String getName();

    /**
     * Gets the unique identifier (UUID) of the guild member.
     *
     * @return the UUID of the member, never null.
     */
    @NotNull UUID getUUID();

    /**
     * Gets the role of the guild member.
     * The role defines the member's permissions and responsibilities within the guild.
     *
     * @return the role of the member, never null.
     */
    @NotNull UUID getRoleId();

    /**
     * Checks if the guild member has his chat toggled.
     * @return true if the guild member has his chat toggled, false otherwise.
     */
    boolean hasChatToggled();

    /**
     * Checks if the guild member has a specific permission in the given guild.
     *
     * @param guild the guild to check the permission in, must not be null.
     * @param permission the permission to check, must not be null.
     * @return true if the member has the specified permission, false otherwise.
     */
    boolean hasPermission(@NotNull Guild<P> guild, @NotNull GuildRole.Permission permission);

    /**
     * Toggles the guild member's chat on or off.
     */
    void toggleGuildChat();
}