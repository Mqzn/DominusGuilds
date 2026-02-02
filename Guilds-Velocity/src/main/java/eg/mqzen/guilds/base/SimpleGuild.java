package eg.mqzen.guilds.base;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildInviteList;
import eg.mqzen.guilds.GuildMOTD;
import eg.mqzen.guilds.GuildManager;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.GuildOwnerInfo;
import eg.mqzen.guilds.GuildRole;
import eg.mqzen.guilds.GuildTag;
import eg.mqzen.guilds.DominusGuilds;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author Mqzen
 * @date 18/5/2025
 */
@SuppressWarnings("unused")
public final class SimpleGuild implements Guild<Player> {

    private final UUID id;
    private String name;
    private final Date foundationDate;
    private GuildTag tag;

    private GuildOwnerInfo owner;
    private final Map<UUID, GuildMember<Player>> members;

    private @NotNull GuildRole defaultRole;
    private @NotNull GuildRole highestRole;

    private final Map<UUID, GuildRole> rolesById;
    private final Map<String, GuildRole> rolesByName = new HashMap<>();

    private final Set<UUID> allies, enemies;

    private final GuildInviteList<Player> inviteList;

    private @Nullable GuildMOTD guildMOTD = null;

    private final DominusGuilds plugin;
    public SimpleGuild(
            DominusGuilds plugin,
            UUID id, String name, Date foundationDate,
            GuildTag tag, GuildOwnerInfo owner,
            Map<UUID, GuildMember<Player>> members,
            Map<UUID, GuildRole> rolesById,
            Set<UUID> allies,
            Set<UUID> enemies
    ) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.foundationDate = foundationDate;
        this.owner = owner;
        this.tag = tag;
        this.members = new HashMap<>(members);
        this.rolesById = new HashMap<>(rolesById);

        for(GuildRole role : rolesById.values()) {
            rolesByName.put(role.getName(), role);
        }

        calculateDefaultRole();
        calculateHighestRole();

        if(!members.containsKey(owner.getUUID())) {
            GuildMember<Player> ownerMember = new SimpleGuildMember(owner.getUUID(), owner.getName(), highestRole.getID());
            this.members.put(owner.getUUID(), ownerMember);
        }
        this.inviteList = new SimpleGuildInviteList(this, plugin);

        this.allies = allies != null ? new HashSet<>(allies) : new HashSet<>();
        this.enemies = enemies != null ? new HashSet<>(enemies) : new HashSet<>();
    }

    public SimpleGuild(DominusGuilds plugin, String name, GuildOwnerInfo owner) {
        this(plugin, UUID.randomUUID(), name, new Date(), SimpleGuildTag.DEFAULT_TAG, owner, new HashMap<>(), DefaultGuildRole.ALL, new HashSet<>(), new HashSet<>());
    }

    private void calculateDefaultRole() {
        if (rolesById.isEmpty()) {
            defaultRole = DefaultGuildRole.MEMBER;
            rolesById.put(defaultRole.getID(), defaultRole);
        } else {
            defaultRole = rolesById.values().stream()
                    .min(Comparator.comparingInt(GuildRole::getWeight))
                    .orElse(DefaultGuildRole.MEMBER);
        }
    }

    private void calculateHighestRole() {
        if (rolesById.isEmpty()) {
            highestRole = DefaultGuildRole.LEADER;
            rolesById.put(highestRole.getID(), highestRole);
        } else {
            highestRole = rolesById.values().stream()
                    .max(Comparator.comparingInt(GuildRole::getWeight))
                    .orElse(DefaultGuildRole.LEADER);
        }
    }

    /**
     * Gets the unique identifier of the guild.
     *
     * @return the UUID of the guild, never null.
     */
    @Override
    public @NotNull UUID getID() {
        return id;
    }

    /**
     * Gets the name of the guild.
     *
     * @return the name of the guild, never null.
     */
    @Override
    public @NotNull String getName() {
        return name;
    }

    /**
     * Sets the name of the guild.
     *
     * @param name the new name for the guild, must not be null or empty.
     */
    @Override
    public void setName(String name) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Guild name must not be null or empty.");
        }
        this.name = name;
    }

    /**
     * @return The date when this guild was created.
     */
    @Override
    public Date getFoundationDate() {
        return foundationDate;
    }

    /**
     * @return The MOTD of the guild.
     */
    @Override
    public @Nullable GuildMOTD getMOTD() {
        return guildMOTD;
    }

    /**
     * Sets a {@link GuildMOTD} for the guild.
     *
     * @param motd the motd info
     */
    @Override
    public void setMOTD(@Nullable GuildMOTD motd) {
        this.guildMOTD = motd;
    }

    /**
     * Gets the tag associated with the guild.
     * The tag is used as a prefix or identifier for the guild.
     *
     * @return the guild's tag, never null.
     */
    @Override
    public @NotNull GuildTag getTag() {
        return tag;
    }

    /**
     * Sets the tag for the guild.
     * The tag is used as a prefix or identifier for the guild.
     *
     * @param tag the new tag for the guild, must not be null.
     */
    @Override
    public void setTag(@NotNull GuildTag tag) {
        this.tag = tag;
    }

    /**
     * Gets the unique identifier of the guild's owner.
     * The owner is a member with the highest level of permissions in the guild.
     *
     * @return the UUID of the guild's owner, never null.
     */
    @Override
    public @NotNull GuildOwnerInfo getOwnerInfo() {
        return owner;
    }

    /**
     * Sets the owner information for the guild.
     * The owner is a member with the highest level of permissions in the guild.
     *
     * @param ownerInfo the new owner information, must not be null.
     */
    @Override
    public void setOwnerInfo(@NotNull GuildOwnerInfo ownerInfo) {
        this.owner = ownerInfo;
    }


    /**
     * Gets the invite list associated with the guild.
     * The invite list contains players who have been invited to join the guild.
     *
     * @return the guild's invite list, never null.
     */
    @Override
    public @NotNull GuildInviteList<Player> getInviteList() {
        return inviteList;
    }

    /**
     * Gets the owner of the guild as a GuildMember.
     * The owner is a member with the highest level of permissions in the guild.
     *
     * @return the guild's owner as a GuildMember, never null.
     */
    @Override
    public @NotNull GuildMember<Player> getOwner() {
        return members.get(owner.getUUID());
    }

    /**
     * Retrieves a member of the guild by their unique identifier.
     *
     * @param uuid the UUID of the member to retrieve, must not be null.
     * @return an Optional containing the guild member if found, or an empty Optional if not found.
     */
    @Override
    public Optional<GuildMember<Player>> getMember(@NotNull UUID uuid) {
        return Optional.ofNullable(members.get(uuid));
    }

    /**
     * Retrieves a member of the guild by their name.
     *
     * @param name the name of the member to retrieve, must not be null.
     * @return an Optional containing the guild member if found, or an empty Optional if not found.
     */
    @Override
    public Optional<GuildMember<Player>> getMemberByName(String name) {
        //TODO add another cache for names to UUIDs (not needed for now)
        for (GuildMember<Player> member : members.values()) {
            if (member.getName().equalsIgnoreCase(name)) {
                return Optional.of(member);
            }
        }
        return Optional.empty();
    }

    /**
     * Adds a new member to the guild.
     *
     * @param member the guild member to be added, must not be null.
     */
    @Override
    public void addMember(@NotNull GuildMember<Player> member) {
        members.put(member.getUUID(), member);
    }

    /**
     * Removes a member from the guild.
     * This method removes the member associated with the specified unique identifier (UUID) from the guild's member list.
     *
     * @param memberUUID the unique identifier (UUID) of the member to be removed, must not be null.
     */
    @Override
    public void removeMember(@NotNull UUID memberUUID) {
        members.remove(memberUUID);
    }

    /**
     * Retrieves all members of the guild.
     *
     * @return a collection containing all guild members. Each member is represented as a
     * {@link GuildMember} or a subtype. The collection is never null and may be empty
     * if the guild has no members.
     */
    @Override
    public Collection<? extends GuildMember<Player>> getMembers() {
        return members.values();
    }

    /**
     * Retrieves all roles within the guild.
     *
     * @return a collection containing all guild roles. Each role is represented as a
     * {@link GuildRole} or
     */
    @Override
    public Collection<? extends GuildRole> getRoles() {
        return rolesById.values();
    }

    /**
     * Retrieves a guild role by its name.
     *
     * @param name the name of the guild role to retrieve must not be null.
     * @return an Optional containing the guild role if found, otherwise an empty Optional.
     */
    @Override
    public Optional<GuildRole> getRoleByName(@NotNull String name) {
        return Optional.ofNullable(rolesByName.get(name));
    }

    /**
     * Retrieves a role within the guild by its unique identifier.
     *
     * @param id the unique identifier (UUID) of the role to retrieve must not be null.
     * @return an Optional containing the GuildRole if found, or an empty Optional if no role with the given ID exists.
     */
    @Override public Optional<GuildRole> getRoleByID(@NotNull UUID id) {
        return Optional.ofNullable(rolesById.get(id));
    }

    /**
     * Adds a new role to the guild.
     *
     * @param role the guild role to be added, must not be null.
     */
    @Override
    public void addRole(@NotNull GuildRole role) {
        rolesById.put(role.getID(), role);
        rolesByName.put(role.getName(), role);
    }

    /**
     * Removes a role from the guild.
     * This method removes the role associated with the specified unique identifier (UUID) from the guild's role list.
     *
     * @param roleUUID the unique identifier (UUID) of the role to be removed, must not be null.
     */
    @Override
    public void removeRoleById(@NotNull UUID roleUUID) {

        if(rolesById.size() == 1 ) {
            System.out.println("Cannot remove the last role in the guild. A guild must have at least one role.");
            return;
        }

        GuildRole role = rolesById.remove(roleUUID);
        if(role != null) {
            rolesByName.remove(role.getName());
        }

    }

    @Override
    public @NotNull Set<UUID> getAlliedGuilds() {
        return allies;
    }

    @Override
    public @NotNull Set<UUID> getEnemyGuilds() {
        return enemies;
    }

    /**
     * Removes a role from the guild by its name.
     * @param name the name of the role to be removed must not be null.
     */
    @Override
    public void removeRoleByName(@NotNull String name) {
        if(rolesById.size() == 1 ) {
            System.out.println("Cannot remove the last role in the guild. A guild must have at least one role.");
            return;
        }

        GuildRole role = rolesByName.remove(name);
        if(role != null) {
            rolesById.remove(role.getID());
        }
    }

    @Override
    public void broadcast(@NotNull GuildManager<Player> guildManager, @NotNull String message, Predicate<GuildMember<Player>> filter) {
        for(GuildMember<Player> member : members.values()) {
            if(filter.test(member)) {
                Optional<Player> playerOpt = ((SimpleGuildManager)guildManager).getPlugin().getServer().getPlayer(member.getUUID());
                playerOpt.ifPresent(player -> player.sendRichMessage(message));
            }
        }

        guildManager.publishGuildBroadcastMessage(this, message);
    }
    
    @Override
    public void broadcast(
            GuildManager<Player> guildManager,
            @NotNull Component message,
            Predicate<GuildMember<Player>> filter
    ) {
        for(GuildMember<Player> member : members.values()) {
            if(filter.test(member)) {
                Optional<Player> playerOpt = ((SimpleGuildManager)guildManager).getPlugin().getServer().getPlayer(member.getUUID());
                playerOpt.ifPresent(player -> player.sendMessage(message));
            }
        }
    }
    
    @Override
    public Guild<Player> copy() {
        return new SimpleGuild(this.plugin, this.id, this.name, this.foundationDate, this.tag, this.owner,
                new HashMap<>(this.members), new HashMap<>(this.rolesById), new HashSet<>(this.allies), new HashSet<>(this.enemies));
    }

    @Override
    public void clearRoles() {
        rolesById.clear();
        rolesByName.clear();
    }

    /**
     * Gets the lowest role in the guild.
     *
     * @return the lowest role in the guild, never null.
     */
    @Override
    public @NotNull GuildRole getDefaultRole() {
        return defaultRole;
    }

    @Override
    public @NotNull GuildRole getHighestRole() {
        return highestRole;
    }


}
