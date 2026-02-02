package eg.mqzen.guilds.event;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.database.GuildUpdateAction;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;

/**
 * Event fired when a guild undergoes a change (creation, update, deletion).
 * This event uses Velocity's ResultedEvent interface for proper cancellation support.
 */
@SuppressWarnings("unused")
public final class GuildChangeEvent extends GuildEvent implements ResultedEvent<ResultedEvent.GenericResult> {

    @Getter
    private final GuildUpdateAction action;
    private GenericResult result = GenericResult.allowed();

    public GuildChangeEvent(Guild<Player> guild, String serverId, GuildUpdateAction action) {
        super(guild, serverId);
        this.action = action;
    }

    @Override
    public GenericResult getResult() {
        return result;
    }

    @Override
    public void setResult(GenericResult result) {
        this.result = result;
    }

    /**
     * Check if the event is cancelled.
     * @return true if the event is cancelled, false otherwise
     */
    public boolean isCancelled() {
        return !result.isAllowed();
    }

    /**
     * Set the cancellation state of the event.
     * @param cancelled true to cancel the event, false to allow it
     */
    public void setCancelled(boolean cancelled) {
        this.result = cancelled ? GenericResult.denied() : GenericResult.allowed();
    }
}
