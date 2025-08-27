package com.rivemc.guilds.base;

import com.rivemc.guilds.DistinctTagTracker;
import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildTag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleDistinctTagTracker implements DistinctTagTracker {

    private final Map<String, UUID> guildTags = new ConcurrentHashMap<>();

    /**
     * Adds a distinct tag for a guild, using the guild's current tag's value and the guild's unique identifier.
     *
     * @param guild The guild from which to derive the tag.
     * @see Guild#getTag()
     * @see GuildTag#getPlainValue()
     */
    @Override
    public void addTagFrom(@NotNull Guild<?> guild) {
        GuildTag tag = guild.getTag();
        String tagValue = tag.getPlainValue();
        if (!tagValue.isEmpty()) {
            addTag(tagValue, guild.getID());
        }
    }

    @Override
    public void addTag(@NotNull String tag, @NotNull UUID guildId) {
        guildTags.put(tag, guildId);
    }

    @Override
    public void removeTag(@NotNull String tag, @NotNull UUID guildId) {
        guildTags.remove(tag);
    }

    @Override
    public void updateTag(@NotNull String oldTag, @NotNull String newTag, @NotNull UUID guildId) {
        guildTags.remove(oldTag);
        addTag(newTag, guildId);
    }

    @Override
    public boolean isTaken(@NotNull String tag) {
        return guildTags.containsKey(tag);
    }

}
