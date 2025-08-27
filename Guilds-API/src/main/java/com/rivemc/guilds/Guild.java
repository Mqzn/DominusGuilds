package com.rivemc.guilds;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Represents a guild in the system.
 * A guild is a group or organization with a unique identifier, name, tag, owner, and members.
 */
public interface Guild<P> {

    /**
     * Gets the unique identifier of the guild.
     *
     * @return the UUID of the guild, never null.
     */
    @NotNull UUID getID();

    /**
     * Gets the name of the guild.
     *
     * @return the name of the guild, never null.
     */
    @NotNull String getName();

    /**
     * Sets the name of the guild.
     *
     * @param name the new name for the guild, must not be null or empty.
     */
    void setName(String name);

    /**
     * @return The date when this guild was created.
     */
    Date getFoundationDate();

    /**
     * @return The MOTD of the guild.
     */
    @Nullable GuildMOTD getMOTD();

    /**
     * Sets a {@link GuildMOTD} for the guild.
     * @param motd the motd info
     */
    void setMOTD(@Nullable GuildMOTD motd);

    default void resetMOTD() {
        setMOTD(null);
    }


    /**
     * Gets the tag associated with the guild.
     * The tag is used as a prefix or identifier for the guild.
     *
     * @return the guild's tag, never null.
     */
    @NotNull GuildTag getTag();

    /**
     * Sets the tag for the guild.
     * The tag is used as a prefix or identifier for the guild.
     *
     * @param tag the new tag for the guild, must not be null.
     */
    void setTag(@NotNull GuildTag tag);

    /**
     * Gets the unique identifier of the guild's owner.
     * The owner is a member with the highest level of permissions in the guild.
     *
     * @return the UUID of the guild's owner, never null.
     */
    @NotNull GuildOwnerInfo getOwnerInfo();

    /**
     * Sets the owner information for the guild.
     * The owner is a member with the highest level of permissions in the guild.
     *
     * @param ownerInfo the new owner information, must not be null.
     */
    void setOwnerInfo(@NotNull GuildOwnerInfo ownerInfo);

    /**
     * Gets the invite list associated with the guild.
     * The invite list contains players who have been invited to join the guild.
     *
     * @return the guild's invite list, never null.
     */
    @NotNull GuildInviteList<P> getInviteList();

    /**
     * Gets the owner of the guild as a GuildMember.
     * The owner is a member with the highest level of permissions in the guild.
     *
     * @return the guild's owner as a GuildMember, never null.
     */
    @NotNull GuildMember<P> getOwner();

    /**
     * Retrieves a member of the guild by their unique identifier.
     *
     * @param uuid the UUID of the member to retrieve, must not be null.
     * @return an Optional containing the guild member if found, or an empty Optional if not found.
     */
    Optional<GuildMember<P>> getMember(@NotNull UUID uuid);

    /**
     * Retrieves a member of the guild by their name.
     *
     * @param name the name of the member to retrieve, must not be null.
     * @return an Optional containing the guild member if found, or an empty Optional if not found.
     */
    Optional<GuildMember<P>> getMemberByName(String name);


    /**
     * Adds a new member to the guild.
     *
     * @param member the guild member to be added, must not be null.
     */
    void addMember(@NotNull GuildMember<P> member);

    /**
     * Removes a member from the guild.
     * This method removes the member associated with the specified unique identifier (UUID) from the guild's member list.
     *
     * @param memberUUID the unique identifier (UUID) of the member to be removed must not be null.
     */
    void removeMember(@NotNull UUID memberUUID);

    /**
     * Retrieves all members of the guild.
     *
     * @return a collection containing all guild members. Each member is represented as a
     *         {@link GuildMember} or a subtype. The collection is never null and may be empty
     *         if the guild has no members.
     */
    Collection<? extends GuildMember<P>> getMembers();


    /**
     * Retrieves all roles within the guild.
     *
     * @return a collection containing all guild roles. Each role is represented as a
     *         {@link GuildRole} or*/
    Collection<? extends GuildRole> getRoles();

    /**
     * Retrieves a guild role by its name.
     *
     * @param name the name of the guild role to retrieve must not be null.
     * @return an Optional containing the guild role if found, otherwise an empty Optional.
     */
    Optional<GuildRole> getRoleByName(@NotNull String name);

    /**
     * Retrieves a role within the guild by its unique identifier.
     *
     * @param id the unique identifier (UUID) of the role to retrieve must not be null.
     * @return an Optional containing the GuildRole if found, or an empty Optional if no role with the given ID exists.
     */
    Optional<GuildRole> getRoleByID(@NotNull UUID id);

    /**
     * Adds a new role to the guild.
     *
     * @param role the guild role to be added, must not be null.
     */
    void addRole(@NotNull GuildRole role);

    /**
     * Removes a role from the guild.
     * This method removes the role associated with the specified unique identifier (UUID) from the guild's role list.
     *
     * @param roleUUID the unique identifier (UUID) of the role to be removed, must not be null.
     */
    void removeRoleById(@NotNull UUID roleUUID);


    //remove by name
    /**
     * Removes a role from the guild by its name.
     *
     * @param name the name of the role to be removed, must not be null.
     */
    void removeRoleByName(@NotNull String name);

    default void broadcast(GuildManager<P> guildManager, @NotNull String message) {
        broadcast(guildManager, message, member -> true);
    }
    
    default void broadcast(GuildManager<P> guildManager, @NotNull Component message) {
        broadcast(guildManager, message, member -> true);
    }

    void broadcast(GuildManager<P> guildManager, @NotNull String message, Predicate<GuildMember<P>> filter);
    void broadcast(GuildManager<P> guildManager, @NotNull Component message, Predicate<GuildMember<P>> filter);

    default @NotNull GuildRole getMemberRole(@NotNull UUID memberUUID) {
        return getMember(memberUUID)
                .map(this::getMemberRole)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

    default @NotNull GuildRole getMemberRole(@NotNull GuildMember<P> member) {
        return getRoleByID(member.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
    }

    Guild<P> copy();

    void clearRoles();

    /**
     * Gets the highest role in the guild.
     * The highest role is typically the role with the most permissions or the one that has the highest rank.
     *
     * @return the highest role in the guild, never null.
     */
    @NotNull GuildRole getHighestRole();

    /**
     * Gets the lowest role in the guild.
     *
     * @return the lowest role in the guild, never null.
     */
    @NotNull GuildRole getDefaultRole();

}