package eg.mqzen.guilds.commands;

import studio.mevera.imperat.ImperatConfig;
import studio.mevera.imperat.context.Context;
import studio.mevera.imperat.context.Source;
import studio.mevera.imperat.exception.SelfHandledException;

public class NotInGuildException extends SelfHandledException {

    public NotInGuildException(Context<?> ctx) {
        super(ctx);
    }

    @Override
    public <S extends Source> void handle(ImperatConfig<S> imperat, Context<S> context) {
        context.source().reply("<red>You are not in a guild!");
    }
}
