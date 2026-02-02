package eg.mqzen.guilds.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.context.Context;
import studio.mevera.imperat.context.Source;
import studio.mevera.imperat.exception.OnlyPlayerAllowedException;

public class VelocityPlayer implements Source {
    private final VelocitySource source;
    
    public VelocityPlayer(VelocitySource source, Context<VelocitySource> context) throws OnlyPlayerAllowedException {
        if(source.isConsole()) {
            throw new OnlyPlayerAllowedException(context);
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
