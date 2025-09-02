package com.rivemc.guilds.commands;

import net.kyori.adventure.text.Component;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.adventure.AdventureHelpComponent;
import studio.mevera.imperat.command.tree.help.theme.HelpComponent;

final class HelpComponentCreator {
    
    private HelpComponentCreator() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
    
    static HelpComponent<VelocitySource, Component> createHelpComponent(Component component) {
        return new AdventureHelpComponent<>(component, (src, compToSend)-> src.reply(compToSend));
    }
}
