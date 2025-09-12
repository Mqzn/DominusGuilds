package com.rivemc.guilds.commands;

import studio.mevera.imperat.ImperatConfig;
import studio.mevera.imperat.context.Context;
import studio.mevera.imperat.context.Source;
import studio.mevera.imperat.exception.SelfHandledException;

public class UnknownGuildTagException extends SelfHandledException {


    private final String input;
    public UnknownGuildTagException(Context<?> ctx, String input) {
        super(ctx);
        this.input = input;
    }

    @Override
    public <S extends Source> void handle(ImperatConfig<S> imperat, Context<S> context) {
        context.source().reply("<red>A guild with the tag '" + input + "' does not exist!" );
    }
}
