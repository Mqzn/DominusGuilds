package eg.mqzen.guilds.commands;

import net.kyori.adventure.text.Component;
import studio.mevera.imperat.VelocityCommandSource;
import studio.mevera.imperat.adventure.AdventureHelpComponent;
import studio.mevera.imperat.command.tree.help.theme.HelpComponent;

final class HelpComponentCreator {
    
    private HelpComponentCreator() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
    
    static HelpComponent<VelocityCommandSource, Component> createHelpComponent(Component component) {
        return new AdventureHelpComponent<>(component, VelocityCommandSource::reply);
    }
}
