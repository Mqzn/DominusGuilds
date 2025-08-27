package com.rivemc.guilds.base;

import com.rivemc.guilds.GuildTag;

import org.jetbrains.annotations.NotNull;

/**
 * @author Mqzen
 * @date 18/5/2025
 */
public final class SimpleGuildTag implements GuildTag {

    private String value;
    private Color color;

    public final static GuildTag DEFAULT_TAG = new SimpleGuildTag("G", SimpleGuildColor.WHITE);

    public SimpleGuildTag(String value, Color color) {
        this.value = value;
        this.color = color;
    }


    /**
     * Gets the plain text value of the guild tag.
     * This value represents the raw, unformatted text of the tag.
     *
     * @return the plain text value of the tag, never null.
     */
    @Override
    public @NotNull String getPlainValue() {
        return value;
    }

    /**
     * Sets the plain text value of the guild tag.
     * This method updates the tag's text without any formatting.
     *
     * @param value the new plain text value for the tag, must not be null.
     */
    @Override
    public void setPlainValue(@NotNull String value) {
        this.value = value;
    }

    /**
     * Gets the color associated with the guild tag.
     * The color is used for styling the tag in various contexts.
     *
     * @return the color of the tag, never null.
     */
    @Override
    public @NotNull Color getColor() {
        return color;
    }


    /**
     * Sets the color of the guild tag.
     *
     * @param color the new color to set for the tag, must not be null.
     */
    @Override
    public void setColor(@NotNull Color color) {
        this.color = color;
    }


}
