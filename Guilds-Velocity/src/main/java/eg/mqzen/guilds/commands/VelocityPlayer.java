package eg.mqzen.guilds.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.VelocityCommandSource;
import studio.mevera.imperat.exception.CommandException;

public class VelocityPlayer implements studio.mevera.imperat.context.CommandSource {
    private final VelocityCommandSource source;
    
    public VelocityPlayer(VelocityCommandSource source) throws CommandException {
        if(source.isConsole()) {
            throw new CommandException("<red>Only players can do this!");
        }
        this.source = source;
    }
    
    
    @Override
    public String name() {
        return source.name();
    }
    
    @Override
    public CommandSource origin() {
        return source.origin();
    }
    
    @Override
    public void reply(String message) {
        source.reply(message);
    }
    
    @Override
    public void warn(String message) {
       source.warn(message);
    }
    
    @Override
    public void error(String message) {
        source.error(message);
    }
    
    @Override
    public boolean isConsole() {
        return false;
    }
    
    public Player asPlayer() {
        return source.asPlayer();
    }
}
