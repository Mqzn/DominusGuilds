package eg.mqzen.guilds.commands;

import studio.mevera.imperat.context.CommandContext;
import studio.mevera.imperat.context.CommandSource;
import studio.mevera.imperat.exception.SelfHandlingException;

public final class InvalidDurationException extends SelfHandlingException {
    private final String input;
    public InvalidDurationException(String input) {
        super();
        this.input = input;
    }

    @Override
    public <S extends CommandSource> void handle(CommandContext<S> context) {
        context.source().reply("<red>Invalid duration: " + input);

    }
}
