package eg.mqzen.guilds;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a tag associated with a guild.
 * A guild tag typically includes a plain text value and a color for display purposes.
 */
public interface GuildTag {

    /**
     * Gets the plain text value of the guild tag.
     * This value represents the raw, unformatted text of the tag.
     *
     * @return the plain text value of the tag, never null.
     */
    @NotNull String getPlainValue();

    /**
     * Sets the plain text value of the guild tag.
     * This method updates the tag's text without any formatting.
     *
     * @param value the new plain text value for the tag, must not be null.
     */
    void setPlainValue(@NotNull String value);

    /**
     * Gets the color associated with the guild tag.
     * The color is used for styling the tag in various contexts.
     *
     * @return the color of the tag, never null.
     */
    @NotNull Color getColor();

    /**
     * Sets the color of the guild tag.
     * @param color the new color to set for the tag, must not be null.
     */
    void setColor(@NotNull Color color);
    
    default Component getAdventureFormattedTag() {
        return Component.text(getPlainValue(), getColor().getAdventureColor());
    }
    
    default String getMiniMessageFormattedTag() {
        String colorName = getColor().getName().toLowerCase();
        return "<" + colorName + ">" + getPlainValue() + "</" + colorName + ">";
    }

    /**
     * Represents the color of a guild tag.
     * Provides compatibility with both Kyori Adventure and Bukkit APIs.
     */
    interface Color {

        /**
         * Gets the color as a Kyori Adventure TextColor.
         *
         * @return the color in Kyori Adventure format, never null.
         */
        @NotNull TextColor getAdventureColor();

        @NotNull String getName();
    }

}