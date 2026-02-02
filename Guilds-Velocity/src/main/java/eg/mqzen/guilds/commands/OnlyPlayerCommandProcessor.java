package eg.mqzen.guilds.commands;


import studio.mevera.imperat.Imperat;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.command.CommandUsage;
import studio.mevera.imperat.command.processors.CommandPreProcessor;
import studio.mevera.imperat.context.Context;
import studio.mevera.imperat.exception.ImperatException;
import studio.mevera.imperat.exception.OnlyPlayerAllowedException;

public class OnlyPlayerCommandProcessor implements CommandPreProcessor<VelocitySource> {
    
    @Override
    public void process(Imperat<VelocitySource> imperat, Context<VelocitySource> context, CommandUsage<VelocitySource> usage) throws ImperatException {
        VelocitySource source = context.source();
        if (source.isConsole()) {
            throw new OnlyPlayerAllowedException(context);
        }
        
    }
}
