package eg.mqzen.guilds;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.EnumSet;
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
    enum Permission {

        //can ally/enemy other guilds
        MANAGE_GUILD_RELATIONS("guilds.other_guild_relations", "Allows managing alliances and rivalries with other guilds"),

        //can invite
        INVITE_OUTSIDERS("guilds.guild.invite_outsiders", "Allows inviting outsiders to the guild"),

        //can kick
        KICK_MEMBER("guilds.member.kick", "Allows kicking members from the guild"),

        //can modify role
        SET_ROLE_MEMBER("guilds.member.setrole", "Allows modifying the role of a guild member"),

        //can promote
        PROMOTE_MEMBER("guilds.member.promote", "Allows promoting a member to a higher role within the guild"),

        //can demote
        DEMOTE_MEMBER("guilds.member.demote", "Allows demoting a member to a lower role within the guild"),

        //can set tag
        SET_TAG("guilds.guild.set_tag", "Allows setting or changing the guild's tag"),

        //can rename
        RENAME_GUILD("guilds.guild.rename", "Allows renaming the guild");

        private final String value;
        private final String description;

        Permission(String value, String description) {
            this.value = value;
            this.description = description;
        }

        /**
         * Gets the value of the permission.
         * The value is a unique identifier for the permission.
         *
         * @return the value of the permission, never null.
         */
        public String getValue() {
            return value;
        }

        /**
         * Gets the description of the permission.
         * The description provides additional context about what the permission allows.
         *
         * @return the description of the permission, never null.
         */
        public String getDescription() {
            return description;
        }

        /**
         * All available permissions.
         */
        public static final Set<Permission> ALL = EnumSet.allOf(Permission.class);

        /**
         * Gets a permission by its value.
         *
         * @param value the permission value to look up
         * @return the permission with the specified value
         * @throws IllegalArgumentException if no permission with the given value exists
         */
        public static @NotNull Permission from(String value) {
            for (Permission permission : values()) {
                if (permission.getValue().equals(value)) {
                    return permission;
                }
            }
            throw new IllegalArgumentException("Unknown permission value: " + value);
        }
    }
}