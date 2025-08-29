package com.rivemc.guilds.base;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.rivemc.guilds.*;
import com.rivemc.guilds.database.GuildStorage;
import com.rivemc.guilds.database.GuildUpdateAction;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Getter
public final class SimpleGuildManager implements GuildManager<Player> {

    private final ProxyServer server;
    private final Logger logger;
    private final GuildStorage<Player> storage;
    private final DistinctTagTracker distinctTagTracker;
    final ReadWriteLock lock = new ReentrantReadWriteLock();

    final Cache<UUID, Guild<Player>> guildsByUUID;
    final Cache<String, Guild<Player>> guildsByName;
    final Cache<UUID, Guild<Player>> playerGuilds;
    
    private final RiveGuilds plugin;
    
    public SimpleGuildManager(
            RiveGuilds plugin,
            GuildStorage<Player> storage,
            DistinctTagTracker distinctTagTracker
    ) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.logger = plugin.getLogger();
        this.storage = storage;
        this.distinctTagTracker = distinctTagTracker;

        this.guildsByUUID = setupGuildCache();
        this.guildsByName = setupNameCache();
        this.playerGuilds = setupPlayerCache();

        initializeRedisSubscription();
        loadAllGuilds();
    }

    private Cache<UUID, Guild<Player>> setupGuildCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .removalListener((RemovalListener<UUID, Guild<Player>>) (guildId, guildToRemove, cause) -> {
                    if (guildToRemove == null) return;
                    for (GuildMember<Player> member : guildToRemove.getMembers()) {
                        playerGuilds.invalidate(member.getUUID());
                    }
                    guildsByName.invalidate(guildToRemove.getName());
                    distinctTagTracker.removeTag(guildToRemove.getTag().getPlainValue(), guildToRemove.getID());
                })
                .build();
    }

    private Cache<String, Guild<Player>> setupNameCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build();
    }

    private Cache<UUID, Guild<Player>> setupPlayerCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build();
    }

    private void initializeRedisSubscription() {
        /*redisHandler.subscribeToGuildUpdates(message -> {
            Guild<Player> guild = gson.fromJson(message.getGuildJson(), SimpleGuild.class);
            switch (message.getAction()) {
                case UPDATE -> updateGuildInCache(guild);
                case DELETE -> removeGuildFromCache(guild.getID());
            }
        });*/
    }

    private void updateGuildInCache(Guild<Player> guild) {
        guildsByUUID.put(guild.getID(), guild);
        guildsByName.put(guild.getName().toLowerCase(), guild);
        for (GuildMember<Player> member : guild.getMembers()) {
            playerGuilds.put(member.getUUID(), guild);
        }
    }

    private void removeGuildFromCache(UUID guildId) {
        guildsByUUID.invalidate(guildId);
    }
    

    @Override
    public @NotNull Optional<Guild<Player>> getGuildByName(@NotNull String guildName) {
        return Optional.ofNullable(guildsByName.getIfPresent(guildName));
    }

    @Override
    public @NotNull Optional<Guild<Player>> getGuildByID(@NotNull UUID guildID) {
        return Optional.ofNullable(guildsByUUID.getIfPresent(guildID));
    }

    @Override
    public @NotNull FutureOperation<Guild<Player>> createGuild(@NotNull String guildName, @NotNull GuildOwnerInfo owner) {
        Guild<Player> guild = new SimpleGuild(plugin, guildName, owner);
        Optional<Player> guildOwnerPlayer = server.getPlayer(owner.getUUID());

        return FutureOperation.of(storage.save(guild))
                .sendErrorMessage(guildOwnerPlayer.orElse(null), "Failed to create guild '" + guildName + "'")
                .onSuccess(savedGuild -> {
                    lock.writeLock().lock();
                    try {
                        guildsByUUID.put(guild.getID(), guild);
                        guildsByName.put(guild.getName().toLowerCase(), guild);
                        playerGuilds.put(owner.getUUID(), guild);
                        distinctTagTracker.addTagFrom(guild);

                        //useRedis(rm -> rm.publishGuildChange(GuildUpdateAction.CREATE_GUILD, savedGuild));
                    } finally {
                        lock.writeLock().unlock();
                    }
                });
    }

    @Override
    public FutureOperation<Void> deleteGuild(@NotNull Guild<Player> guild) {
        lock.writeLock().lock();
        try {
            //we remove its members first
            guildsByUUID.invalidate(guild.getID());
            //guildsByName.invalidate(guild.getName());
        } finally {
            lock.writeLock().unlock();
        }
        
        var guildOwner = guild.getOwner();
        Optional<Player> guildOwnerPlayer = server.getPlayer(guildOwner.getUUID());
        
        return FutureOperation.of(storage.delete(guild.getID()))
                .sendErrorMessage(guildOwnerPlayer.orElse(null), "Failed to disband guild '" + guild.getName() + "'")
                .onSuccess(()-> {
                    //Publish to Redis
                    //useRedis((rm)-> rm.publishGuildChange(GuildUpdateAction.DISBAND_GUILD, cloneGuild));
                });
    }

    @Override
    public @NotNull Optional<Guild<Player>> getPlayerGuild(@NotNull UUID playerUUID) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(playerGuilds.getIfPresent(playerUUID));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Retrieves the guild a player is a member of, by the player's name.
     *
     * @param playerName The name of the player.
     * @return An Optional containing the guild if the player is in one, or an empty Optional if not.
     */
    @Override
    public @NotNull Optional<Guild<Player>> getPlayerGuild(@NotNull String playerName) {
        lock.readLock().lock();
        try {
            for(Guild<Player> guild : guildsByUUID.asMap().values()) {
                for (GuildMember<Player> member : guild.getMembers()) {
                    if (member.getName().equalsIgnoreCase(playerName)) {
                        return Optional.of(guild);
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return Optional.empty();
    }

    /**
     * Retrieves the guild a player is a member of.
     *
     * @param player The player.
     * @return An Optional containing the guild if the player is in one, or an empty Optional if not.
     */
    @Override
    public @NotNull Optional<Guild<Player>> getPlayerGuild(Player player) {
        return getPlayerGuild(player.getUniqueId());
    }

    @Override
    public @NotNull GuildStorage<Player> getStorage() {
        return storage;
    }

    @Override
    public @NotNull DistinctTagTracker getDistinctTagTracker() {
        return distinctTagTracker;
    }


    @Override
    public FutureOperation<Guild<Player>> updateGuild(
            @NotNull GuildUpdateAction updateAction,
            @NotNull UUID oldGuildId,
            Guild<Player> newGuild,
            boolean databaseUpdate
    ) {
        return FutureOperation.of(
                CompletableFuture.supplyAsync(() -> {
                    lock.writeLock().lock();
                    try {
                        Guild<Player> oldGuild = guildsByUUID.getIfPresent(oldGuildId);
                        if (oldGuild == null) {
                            throw new IllegalArgumentException("Guild not found: " + oldGuildId);
                        }

                        // Create backup for potential rollback
                        String oldGuildName = oldGuild.getName();

                        Set<GuildRole> oldRoles = new HashSet<>(newGuild.getRoles());

                        Set<UUID> oldMemberUUIDs = oldGuild.getMembers().stream()
                                .map(GuildMember::getUUID).collect(Collectors.toSet());

                        newGuild.clearRoles();
                        for (GuildRole role : oldRoles) {
                            newGuild.addRole(role);
                        }

                        try {
                            // Update guild maps
                            guildsByUUID.put(oldGuildId, newGuild);

                            guildsByName.invalidate(oldGuildName);
                            guildsByName.put(newGuild.getName(), newGuild);


                            // Remove old member mappings
                            playerGuilds.invalidateAll(oldMemberUUIDs);

                            // Add new member mappings
                            for (GuildMember<Player> member : newGuild.getMembers()) {
                                playerGuilds.put(member.getUUID(), newGuild);
                            }

                            return newGuild;
                        } catch (Exception e) {
                            // Rollback on failure
                            guildsByUUID.put(oldGuildId, oldGuild);
                            guildsByName.invalidate(newGuild.getName());
                            guildsByName.put(oldGuildName, oldGuild);

                            // Restore old member mappings
                            for (UUID memberUUID : oldMemberUUIDs) {
                                playerGuilds.put(memberUUID, oldGuild);
                            }

                            throw new RuntimeException("Failed to update guild in memory", e);
                        }
                    } finally {
                        lock.writeLock().unlock();
                    }
                }).thenCompose(guild -> {
                    //useRedis( (rm)-> rm.publishGuildChange(updateAction, guild));
                    if (databaseUpdate) {
                        return storage.save(guild);
                    } else {
                        return CompletableFuture.completedFuture(guild);
                    }
                })
        );
    }

    @Override
    public void publishGuildChatMessage(@NotNull Guild<Player> guild, @NotNull UUID senderId, @NotNull String message) {
        //useRedis( (rm)-> rm.publishGuildChatMessage(guild, senderId, message));
        
    }

    /*private void useRedis(Consumer<RedisConnectionHandler> consumer) {
        if (redisHandler != null) {
            consumer.accept(redisHandler);
        } else {
            logger.warn("Redis handler is not initialized. Redis functionality is disabled.");
        }
    }*/

    @Override
    public void publishGuildBroadcastMessage(@NotNull Guild<Player> guild, @NotNull String message) {
        //useRedis( (rm)-> rm.publishGuildBroadcast(guild, message));
        for(GuildMember<Player> member : guild.getMembers()) {
            Optional<Player> playerOpt = server.getPlayer(member.getUUID());
            playerOpt.ifPresent(player -> player.sendRichMessage(message));
        }
    }

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
    @Override
    public void publishInviteToProxy(@NotNull Player inviter, @NotNull String targetName, @NotNull Guild<Player> guild) {
        //useRedis( (rm)-> rm.publishGuildInvite(inviter, targetName, guild));
    }

    /**
     * Updates guild in memory only (for Redis sync operations)
     */
    public void updateGuildInMemory(Guild<Player> guild) {
        lock.writeLock().lock();
        try {
            Guild<Player> existing = guildsByUUID.getIfPresent(guild.getID());
            if (existing != null) {
                // Remove old member mappings
                for (GuildMember<Player> member : existing.getMembers()) {
                    playerGuilds.invalidate(member.getUUID());
                }
                guildsByName.invalidate(existing.getName());
            }

            // Add new mappings
            guildsByUUID.put(guild.getID(), guild);
            guildsByName.put(guild.getName(), guild);
            for (GuildMember<Player> member : guild.getMembers()) {
                playerGuilds.put(member.getUUID(), guild);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Removes guild from memory only (for Redis sync operations)
     */
    public void removeGuildFromMemory(UUID guildId) {
        lock.writeLock().lock();
        try {
            Guild<Player> guild = guildsByUUID.getIfPresent(guildId);
            if (guild != null) {
                // Remove member mappings
                for (GuildMember<Player> member : guild.getMembers()) {
                    playerGuilds.invalidate(member.getUUID());
                }

                guildsByUUID.invalidate(guildId);
                guildsByName.invalidate(guild.getName());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void loadAllGuilds() {
        LinkedHashSet<Guild<Player>> loadedGuilds = storage.loadAll(-1).join();

        lock.writeLock().lock();
        try {
            for (Guild<Player> guild : loadedGuilds) {
                guildsByUUID.put(guild.getID(), guild);
                guildsByName.put(guild.getName(), guild);

                for (GuildMember<Player> member : guild.getMembers()) {
                    playerGuilds.put(member.getUUID(), guild);
                }

                // Add the guild tag into the distinct tag tracker (if guild has a non default tag)
                if (!guild.getTag().getPlainValue().equals(SimpleGuildTag.DEFAULT_TAG.getPlainValue()))
                    distinctTagTracker.addTag(guild.getTag().getPlainValue(), guild.getID());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Invites a target player to the source player's guild.
     * <p>
     * This method checks if the target player is already in a guild, verifies the inviter's permissions,
     * and ensures the target has not already been invited. If all conditions are met, the target player
     * is added to the guild's invite list and notified of the invitation.
     *
     * @param source      The player sending the invitation.
     * @param sourceGuild The guild the source player belongs to and is inviting the target to join.
     * @param target  The player being invited to the guild.
     */
    @Override
    public void invitePlayerToGuild(Player source, @NotNull Guild<Player> sourceGuild, @NotNull Player target) {
        lock.writeLock().lock();
        try {
            if (getPlayerGuild(target).isPresent()) {
                source.sendMessage(Component.text("Target '" + target.getUsername() + "' is already in a guild", NamedTextColor.RED));
                return;
            }

            Optional<GuildMember<Player>> guildMember = sourceGuild.getMember(source.getUniqueId());
            if (guildMember.isEmpty()) {
                source.sendMessage(Component.text("An error occurred: Your guild membership could not be verified", NamedTextColor.RED));
                source.sendMessage(Component.text("Please contact an administrator", NamedTextColor.RED));
                return;
            }

            GuildMember<Player> inviter = guildMember.get();
            if (!inviter.hasPermission(sourceGuild, GuildRole.Permission.INVITE_OUTSIDERS)) {
                source.sendMessage(Component.text("You don't have permission to invite players", NamedTextColor.RED));
                return;
            }

            if (sourceGuild.getInviteList().isInvited(target.getUniqueId())) {
                source.sendMessage(Component.text("You have already invited '" + target.getUsername() + "' to your guild", NamedTextColor.RED));
                return;
            }

            sourceGuild.getInviteList().addPlayer(source.getUniqueId(), target.getUniqueId());
            GuildTag tag = sourceGuild.getTag();
            Component formattedTag = Component.text(tag.getPlainValue()).color(tag.getColor().getAdventureColor());

            source.sendMessage(Component.text("You have invited ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text(target.getUsername()))
                    .append(Component.text(" to guild "))
                    .append(formattedTag));

            target.sendMessage(Component.text("You have been invited by ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text(source.getUsername()))
                    .append(Component.text(" to guild "))
                    .append(formattedTag));
            target.sendMessage(Component.text("You have 5 minutes to accept this invite", NamedTextColor.GREEN));
        } finally {
            lock.writeLock().unlock();
        }
    }
}

