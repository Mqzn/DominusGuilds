package eg.mqzen.guilds.base;

import eg.mqzen.guilds.GuildTag;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;


/**
 * @author Mqzen
 * @date 18/5/2025
 */
public enum SimpleGuildColor implements GuildTag.Color {

    WHITE(NamedTextColor.WHITE),
    RED(NamedTextColor.RED),
    DARK_RED(NamedTextColor.DARK_RED),
    YELLOW(NamedTextColor.YELLOW),
    GOLDEN(NamedTextColor.GOLD),
    GREEN(NamedTextColor.GREEN),
    OLIVE(NamedTextColor.DARK_GREEN),
    AQUA(NamedTextColor.AQUA),
    CYAN(NamedTextColor.AQUA),
    BLUE(NamedTextColor.BLUE),
    NAVY(NamedTextColor.DARK_BLUE),
    PINK(NamedTextColor.LIGHT_PURPLE),
    PURPLE(NamedTextColor.DARK_PURPLE),
    SILVER(NamedTextColor.GRAY),
    DARK_GRAY(NamedTextColor.DARK_GRAY),
    BLACK(NamedTextColor.BLACK);

    private final TextColor kyoriColor;

    SimpleGuildColor(TextColor kyoriColor) {
        this.kyoriColor = kyoriColor;
    }

    @Override
    public @NotNull TextColor getAdventureColor() {
        return kyoriColor;
    }

    @Override
    public @NotNull String getName() {
        return name();
    }

}
