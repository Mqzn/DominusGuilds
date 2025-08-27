package com.rivemc.guilds;

import org.jetbrains.annotations.NotNull;
import java.time.Duration;

/**
 * Represents a message of the day that is sent to members when they
 * join the server, the message will stop being sent after a specific duration of time
 * specified by {@link GuildMOTD#getExpiryDuration()}
 */
public interface GuildMOTD {

    /**
     * @return The message of the day.
     */
    @NotNull String getValue();

    /**
     * The default expiry duration is 24 hours (1 day)
     * @return the expiry duration for this message of the day
     */
    @NotNull Duration getExpiryDuration();

}
