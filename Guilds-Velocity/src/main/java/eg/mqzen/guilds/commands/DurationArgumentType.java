package eg.mqzen.guilds.commands;

import eg.mqzen.guilds.util.DurationParser;
import org.jetbrains.annotations.NotNull;
import studio.mevera.imperat.VelocityCommandSource;
import studio.mevera.imperat.command.arguments.type.ArgumentType;
import studio.mevera.imperat.context.CommandContext;
import studio.mevera.imperat.exception.CommandException;

import java.time.Duration;
import java.util.List;

public class DurationArgumentType extends ArgumentType<VelocityCommandSource, Duration> {
    
    public DurationArgumentType() {
        this.suggestions.addAll(List.of("1d", "1h", "30m", "1d12h", "7d", "24h"));
    }

    @Override
    public Duration parse(@NotNull CommandContext<VelocityCommandSource> context, @NotNull String input)
            throws CommandException {
        try {
            return DurationParser.parseDuration(input);
        } catch (IllegalArgumentException e) {
            throw new InvalidDurationException(input);
        }
    }
    
}
