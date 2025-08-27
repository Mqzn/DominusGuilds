package com.rivemc.guilds;

import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a role within a guild.
 * A guild role defines permissions and attributes for members assigned to it.
 */
public interface GuildRole {

    /**
     * Gets the unique identifier of the role.
     *
     * @return the UUID of the role, never null.
     */
    UUID getID();

    /**
     * Gets the name of the role.
     *
     * @return the name of the role, never null.
     */
    String getName();

    /**
     * Gets the prefix associated with the role.
     * The prefix is used for display purposes, such as in chat or UI.
     *
     * @return the prefix of the role, never null.
     */
    String getPrefix();


    /**
     * @return The weight of a guild role
     */
    int getWeight();

    /**
     * Gets all permissions assigned to the role.
     *
     * @return a collection of permissions, never null.
     */
    Collection<? extends Permission> getPermissions();

    /**
     * Checks if the role has a specific permission.
     *
     * @param permission the permission to check, must not be null.
     * @return true if the role has the specified permission, false otherwise.
     */
    boolean hasPermission(@NotNull Permission permission);

    /**
     * Sets a specific permission for the role.
     * This method allows you to grant or modify permissions for the role.
     *
     * @param permission the permission to set must not be null.
     */
    void setPermission(@NotNull Permission permission);

    /**
     * Unsets a specific permission for the role.
     *
     * @param permission the permission to unset must not be null.
     */
    void unsetPermission(@NotNull Permission permission);

    /**
     * Represents a permission associated with a guild role.
     * A permission defines a specific action or capability.
     */
    interface Permission {

        //can invite
        Permission INVITE_OUTSIDERS = of("guilds.guild.invite_outsiders", "Allows inviting outsiders to the guild");

        //can kick
        Permission KICK_MEMBER = of("guilds.member.kick", "Allows kicking members from the guild");

        //can modify role
        Permission SET_ROLE_MEMBER = of("guilds.member.setrole", "Allows modifying the role of a guild member");

        //can promote
        Permission PROMOTE_MEMBER = of("guilds.member.promote", "Allows promoting a member to a higher role within the guild");

        //can demote
        Permission DEMOTE_MEMBER = of("guilds.member.demote", "Allows demoting a member to a lower role within the guild");

        //can set tag
        Permission SET_TAG = of("guilds.guild.set_tag", "Allows setting or changing the guild's tag");

        //can rename
        Permission RENAME_GUILD = of("guilds.guild.rename", "Allows renaming the guild");


        Set<Permission> ALL = Set.of(
                INVITE_OUTSIDERS,
                KICK_MEMBER,
                SET_ROLE_MEMBER,
                PROMOTE_MEMBER,
                DEMOTE_MEMBER,
                SET_TAG,
                RENAME_GUILD
        );

        /**
         * Gets the value of the permission.
         * The value is a unique identifier for the permission.
         *
         * @return the value of the permission, never null.
         */
        String getValue();

        /**
         * Gets the description of the permission.
         * The description provides additional context about what the permission allows.
         *
         * @return the description of the permission, never null.
         */
        String getDescription();

        static Permission of(String value, String description) {
            return new ImmutablePermission(value, description);
        }

        static @NotNull Permission from(String value) {

            for(Permission permission : ALL) {
                if (permission.getValue().equals(value)) {
                    return permission;
                }
            }
            throw new IllegalArgumentException("Unknown permission value: " + value);
        }

    }
    record ImmutablePermission(String value, String description) implements Permission {
        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }


}