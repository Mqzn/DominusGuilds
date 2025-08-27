package com.rivemc.guilds.base;

import com.rivemc.guilds.GuildMOTD;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class SimpleGuildMOTD implements GuildMOTD {

    private final String value;
    private final Duration expiryDuration;

    public SimpleGuildMOTD(String value, Duration expiryDuration) {
        this.value = value;
        this.expiryDuration = expiryDuration;
    }


    /**
     * @return The message of the day.
     */
    @Override
    public @NotNull String getValue() {
        return value;
    }

    /**
     * The default expiry duration is 24 hours (1 day)
     *
     * @return the expiry duration for this message of the day
     */
    @Override
    public @NotNull Duration getExpiryDuration() {
        return expiryDuration;
    }
}
