package com.rivemc.guilds.base;

import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMember;
import com.rivemc.guilds.GuildRole;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

/**
 * @author Mqzen
 * @date 18/5/2025
 */
public class SimpleGuildMember implements GuildMember<Player> {
    private final UUID uuid;
    private final String name;
    private final UUID roleId;
    private boolean hasChatToggled = false;


    public SimpleGuildMember(UUID uuid, String name, UUID roleId) {
        this.uuid = uuid;
        this.name = name;
        this.roleId = roleId;
    }

    public SimpleGuildMember(UUID uuid, String name) {
        this(uuid, name, DefaultGuildRole.MEMBER.getID());
    }

    /**
     * Gets the name of the guild member.
     *
     * @return the name of the member, never null.
     */
    @Override
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets the unique identifier (UUID) of the guild member.
     *
     * @return the UUID of the member, never null.
     */
    @Override
    public @NotNull UUID getUUID() {
        return uuid;
    }

    /**
     * Gets the role of the guild member.
     * The role defines the member's permissions and responsibilities within the guild.
     *
     * @return the role of the member, never null.
     */
    @Override
    public @NotNull UUID getRoleId() {
        return roleId;
    }

    @Override
    public boolean hasChatToggled() {
        return hasChatToggled;
    }


    @Override
    public void toggleGuildChat() {
        hasChatToggled = !hasChatToggled;
    }

    /**
     * Checks if the guild member has a specific permission in the given guild.
     *
     * @param guild           the guild to check the permission in, must not be null.
     * @param permission the permission to check, must not be null.
     * @return true if the member has the specified permission, false otherwise.
     */
    @Override
    public boolean hasPermission(@NotNull Guild<Player> guild, GuildRole.@NotNull Permission permission) {
        return guild.getRoleByID(this.roleId).map((role)-> role.hasPermission(permission)).orElse(false);
    }
}
