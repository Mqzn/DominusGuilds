package eg.mqzen.guilds.commands;

import studio.mevera.imperat.context.CommandContext;
import studio.mevera.imperat.context.CommandSource;
import studio.mevera.imperat.exception.SelfHandlingException;

public class UnknownGuildTagException extends SelfHandlingException {


    private final String input;
    public UnknownGuildTagException(String input) {
        this.input = input;
    }


    @Override
    public <S extends CommandSource> void handle(CommandContext<S> context) {
        context.source().reply("<red>A guild with the tag '" + input + "' does not exist!" );
    }
}
