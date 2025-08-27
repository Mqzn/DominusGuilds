package com.rivemc.guilds.base;

import com.rivemc.guilds.GuildRole;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Mqzen
 * @date 18/5/2025
 */
public final class SimpleGuildRole implements GuildRole {

    private final UUID uniqueId;
    private final String name;

    private final String prefix;
    private final int weight;

    private final Map<String, Permission> permissions;

    /**
     * Constructs a SimpleGuildRole with the specified unique identifier, name, and permissions.
     *
     * @param uniqueId   the unique identifier of the role must not be null.
     * @param name       the name of the role must not be null.
     * @param permissions a map of permissions associated with the role must not be null.
     */
    public SimpleGuildRole(UUID uniqueId, String name, String prefix, int weight, Map<String, Permission> permissions) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.prefix = prefix;
        this.weight = weight;
        this.permissions = new HashMap<>(permissions);
    }

    public SimpleGuildRole(String name, String prefix, int weight, Map<String, Permission> permissions) {
        this(UUID.randomUUID(), name, prefix, weight, permissions);
    }


    public SimpleGuildRole(String name, String prefix, int weight, Set<Permission> permissions) {
        this(UUID.randomUUID(), name, prefix, weight, permissions.stream()
                .collect(HashMap::new, (m, p) -> m.put(p.getValue(), p), HashMap::putAll)
        );
    }

    /**
     * Gets the unique identifier of the role.
     *
     * @return the UUID of the role, never null.
     */
    @Override
    public UUID getID() {
        return uniqueId;
    }

    /**
     * Gets the name of the role.
     *
     * @return the name of the role, never null.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the prefix associated with the role.
     * The prefix is used for display purposes, such as in chat or UI.
     *
     * @return the prefix of the role, never null.
     */
    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return The weight of a guild role
     */
    @Override
    public int getWeight() {
        return weight;
    }

    /**
     * Gets all permissions assigned to the role.
     *
     * @return a collection of permissions, never null.
     */
    @Override
    public Collection<? extends Permission> getPermissions() {
        return permissions.values();
    }


    /**
     * Checks if the role has a specific permission.
     *
     * @param permission the permission to check, must not be null.
     * @return true if the role has the specified permission, false otherwise.
     */
    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return permissions.get(permission.getValue()) != null;
    }

    /**
     * Sets a specific permission for the role.
     * This method allows you to grant or modify permissions for the role.
     *
     * @param permission the permission to set must not be null.
     */
    @Override
    public void setPermission(@NotNull Permission permission) {
        permissions.put(permission.getValue(), permission);
    }

    /**
     * Unsets a specific permission for the role.
     *
     * @param permission the permission to unset must not be null.
     */
    @Override
    public void unsetPermission(@NotNull Permission permission) {
        permissions.remove(permission.getValue());
    }

}
