package com.rivemc.guilds.commands;

import com.rivemc.guilds.util.DurationParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.command.parameters.type.BaseParameterType;
import studio.mevera.imperat.context.ExecutionContext;
import studio.mevera.imperat.context.internal.CommandInputStream;
import studio.mevera.imperat.exception.ImperatException;

import java.time.Duration;
import java.util.List;

public class DurationParameterType extends BaseParameterType<VelocitySource, Duration> {
    
    public DurationParameterType() {
        this.suggestions.addAll(List.of("1d", "1h", "30m", "1d12h", "7d", "24h"));
    }
    
    @Override
    public @Nullable Duration resolve(
            @NotNull ExecutionContext<VelocitySource> context,
            @NotNull CommandInputStream<VelocitySource> inputStream,
            @NotNull String input
    ) throws ImperatException {
        try {
            return DurationParser.parseDuration(input);
        } catch (IllegalArgumentException e) {
            throw new InvalidDurationException(context, input);
        }
    }
    
    
}
