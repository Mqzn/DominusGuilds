package eg.mqzen.guilds.commands;

import studio.mevera.imperat.context.CommandContext;
import studio.mevera.imperat.context.CommandSource;
import studio.mevera.imperat.exception.SelfHandlingException;

public class NotInGuildException extends SelfHandlingException {

    public NotInGuildException() {
        super();
    }

    @Override
    public <S extends CommandSource> void handle(CommandContext<S> context) {
        context.source().reply("<red>You are not in a guild!");
    }

}
