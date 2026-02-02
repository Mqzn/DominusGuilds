package eg.mqzen.guilds.util;

import eg.mqzen.guilds.Guild;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

/**
 * Utility class for formatting guild chat messages using MiniMessage format.
 * This class handles the separation of concerns for message formatting.
 *
 * @author Generated
 */
public class GuildMessageFormatter {
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    
    // MiniMessage template for guild chat messages
    private static final String GUILD_CHAT_TEMPLATE =
        "<color:#<guild_color>>[<guild_name>]</color> <white><player_name></white> <dark_gray>»</dark_gray> <gray><message></gray>";
    
    /**
     * Formats a guild chat message using MiniMessage format.
     *
     * @param guild The guild the message is being sent to
     * @param sender The player sending the message
     * @param message The message content
     * @return A formatted Component ready to be sent to players
     */
    public static Component formatGuildChatMessage(Guild<Player> guild, Player sender, String message) {
        // Get the guild color as a hex string
        String guildColorHex = guild.getTag().getColor().getAdventureColor().asHexString();
        
        // Create the formatted message using MiniMessage with placeholders
        return MINI_MESSAGE.deserialize(GUILD_CHAT_TEMPLATE,
            Placeholder.unparsed("guild_color", guildColorHex),
            Placeholder.unparsed("guild_name", guild.getName()),
            Placeholder.unparsed("player_name", sender.getUsername()),
            Placeholder.unparsed("message", message)
        );
    }
    
    /**
     * Alternative method for when you have the player name as a string instead of Player object.
     *
     * @param guild The guild the message is being sent to
     * @param senderName The name of the player sending the message
     * @param message The message content
     * @return A formatted Component ready to be sent to players
     */
    public static Component formatGuildChatMessage(Guild<Player> guild, String senderName, String message) {
        // Get the guild color as a hex string
        String guildColorHex = guild.getTag().getColor().getAdventureColor().asHexString();
        
        // Create the formatted message using MiniMessage with placeholders
        return MINI_MESSAGE.deserialize(GUILD_CHAT_TEMPLATE,
            Placeholder.unparsed("guild_color", guildColorHex),
            Placeholder.unparsed("guild_name", guild.getName()),
            Placeholder.unparsed("player_name", senderName),
            Placeholder.unparsed("message", message)
        );
    }
    
    /**
     * Creates an error message for guild-related issues.
     *
     * @param message The error message
     * @return A formatted error Component
     */
    public static Component createErrorMessage(String message) {
        return Component.text(message, NamedTextColor.RED);
    }
}
