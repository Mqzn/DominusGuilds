package eg.mqzen.guilds;

import java.util.UUID;

/**
 * Represents information about the owner of a guild.
 * <p>
 * This interface provides methods to retrieve the owner's name and unique identifier (UUID).
 * </p>
 */
public interface GuildOwnerInfo {

    /**
     * Retrieves the name of the guild owner.
     *
     * @return The name of the guild owner as a String.
     */
    String getName();

    /**
     * Retrieves the unique identifier (UUID) of the guild owner.
     *
     * @return The UUID of the guild owner.
     */
    UUID getUUID();
}