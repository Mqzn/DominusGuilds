package eg.mqzen.guilds.event;

import eg.mqzen.guilds.Guild;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;

import java.util.UUID;

/**
 * Event fired when a guild chat message is sent.
 * This event uses Velocity's ResultedEvent interface for proper cancellation support.
 */
@SuppressWarnings("unused")
public final class GuildChatEvent extends GuildEvent implements ResultedEvent<ResultedEvent.GenericResult> {

    @Getter
    private final UUID senderId;
    @Getter
    private final String message;
    private GenericResult result = GenericResult.allowed();

    public GuildChatEvent(Guild<Player> guild, String serverId, UUID senderId, String message) {
        super(guild, serverId);
        this.senderId = senderId;
        this.message = message;
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
