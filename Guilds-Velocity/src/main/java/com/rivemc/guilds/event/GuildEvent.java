package com.rivemc.guilds.event;

import lombok.Getter;
import com.rivemc.guilds.Guild;
import com.velocitypowered.api.proxy.Player;

/**
 * Base class for all guild-related events in the Velocity environment.
 * Unlike Bukkit events, Velocity events are simple POJOs without HandlerList requirements.
 */
public abstract class GuildEvent {

    @Getter private final Guild<Player> guild;
    private final String serverId;

    protected GuildEvent(Guild<Player> guild, String serverId) {
        this.guild = guild;
        this.serverId = serverId;
    }

    /**
     * Returns the ID of the server that triggered this event.
     *
     * @return The server ID.
     */
    public String getSourceServer() {
        return serverId;
    }

}
