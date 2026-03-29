package eg.mqzen.guilds.commands;

import studio.mevera.imperat.context.CommandContext;
import studio.mevera.imperat.context.CommandSource;
import studio.mevera.imperat.exception.SelfHandlingException;

public class InsufficientGuildPermissionException extends SelfHandlingException {

    public InsufficientGuildPermissionException() {
        super();
    }

    @Override
    public <S extends CommandSource> void handle(CommandContext<S> context) {
        context.source().reply("<red>You don't have enough guild authority to do that!");
    }

}
