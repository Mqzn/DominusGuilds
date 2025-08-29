package com.rivemc.guilds.commands;

import net.kyori.adventure.text.Component;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.adventure.AdventureHelpComponent;

final class HelpComponentCreator {
    
    private HelpComponentCreator() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
    
    static AdventureHelpComponent<VelocitySource> createHelpComponent(Component component) {
        return new AdventureHelpComponent<>(component, (src, compToSend)-> src.reply(component));
    }
}
