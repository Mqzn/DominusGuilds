package eg.mqzen.guilds.commands;

import studio.mevera.imperat.context.CommandContext;
import studio.mevera.imperat.context.CommandSource;
import studio.mevera.imperat.exception.SelfHandlingException;


public class TargetNotInAnyGuildException extends SelfHandlingException {

    private final String input;
    public TargetNotInAnyGuildException(String input) {
        super();
        this.input = input;
    }

    @Override public <S extends CommandSource> void handle(CommandContext<S> context) {
        context.source().reply("<red>The specified target '" + input + "' is not in any guild!");
    }
}
