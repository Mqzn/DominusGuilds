package com.rivemc.guilds.commands;

import studio.mevera.imperat.ImperatConfig;
import studio.mevera.imperat.context.Context;
import studio.mevera.imperat.context.Source;
import studio.mevera.imperat.exception.SelfHandledException;

public class TargetNotInAnyGuildException extends SelfHandledException {

    private final String input;
    public TargetNotInAnyGuildException(Context<?> ctx, String input) {
        super(ctx);
        this.input = input;
    }

    @Override public <S extends Source> void handle(ImperatConfig<S> imperat, Context<S> context) {
        context.source().reply("<red>The specified target '" + input + "' is not in any guild!");
    }
}
