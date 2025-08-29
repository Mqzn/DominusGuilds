package com.rivemc.guilds.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.command.Command;
import studio.mevera.imperat.command.CommandUsage;
import studio.mevera.imperat.command.parameters.CommandParameter;
import studio.mevera.imperat.command.tree.help.HelpComponent;
import studio.mevera.imperat.command.tree.help.HelpTheme;
import studio.mevera.imperat.command.tree.help.renderers.UsageFormatter;
import studio.mevera.imperat.context.ExecutionContext;

import java.util.Map;
import java.util.Objects;

public class GuildHelpTheme implements HelpTheme<VelocitySource, Component> {
    
    private final Map<Option, Object> optionValues = Map.of(
            Option.SHOW_FOOTER, true,
            Option.SHOW_HEADER, true
    );
    
    private final HelpComponent<VelocitySource, Component> branch = HelpComponentCreator.createHelpComponent(Component.text("├─ "));
    private final HelpComponent<VelocitySource, Component> lastBranch = HelpComponentCreator.createHelpComponent(Component.text("└─ "));
    private final HelpComponent<VelocitySource, Component> indent = HelpComponentCreator.createHelpComponent(Component.text("│  "));
    private final HelpComponent<VelocitySource, Component> emptyIndent = HelpComponentCreator.createHelpComponent(Component.text("   "));
    
    private final UsageFormatter<VelocitySource, Component> usageFormatter = new EcoUsageFormatter();
    
    @Override
    public HelpComponent<VelocitySource, Component> createEmptyComponent() {
        return HelpComponentCreator.createHelpComponent(Component.empty());
    }
    
    @Override
    public @NotNull PresentationStyle getPreferredStyle() {
        return PresentationStyle.FLAT;
    }
    
    @Override
    public int getIndentMultiplier() {
        return 1;
    }
    
    @Override
    public @NotNull HelpComponent<VelocitySource, Component> getBranch() {
        return branch;
    }
    
    @Override
    public @NotNull HelpComponent<VelocitySource, Component> getLastBranch() {
        return lastBranch;
    }
    
    @Override
    public @NotNull HelpComponent<VelocitySource, Component> getIndent() {
        return indent;
    }
    
    @Override
    public @NotNull HelpComponent<VelocitySource, Component> getEmptyIndent() {
        return emptyIndent;
    }
    
    @Override
    public @NotNull HelpComponent<VelocitySource, Component> getHeader(ExecutionContext<VelocitySource> context) {
        // Create separate hyphen components (don't reuse the same instance)
        Component leftHyphen = Component.text("==========")
                .decorate(TextDecoration.STRIKETHROUGH, TextDecoration.BOLD)
                .color(TextColor.fromHexString("#282863"));
        
        Component rightHyphen = Component.text("==========")
                .decorate(TextDecoration.STRIKETHROUGH, TextDecoration.BOLD)
                .color(TextColor.fromHexString("#282863"));
        
        // Create the middle text component with its own style (no inheritance)
        Component middleText = Component.text(" Guild Commands ")
                .color(TextColor.fromHexString("#ffd700"))
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.STRIKETHROUGH, false);
        
        // Create the legend component with its own style
        Component legend = Component.text("Required:", NamedTextColor.GRAY)
                .decoration(TextDecoration.STRIKETHROUGH, false)
                .decoration(TextDecoration.BOLD, false)
                .append(Component.space())
                .append(Component.text("<>", TextColor.fromHexString("#90d17d")))
                .append(Component.space())
                .append(Component.text("Optional:", NamedTextColor.GRAY))
                .append(Component.space())
                .append(Component.text("[]", TextColor.fromHexString("#deb76a")));
        
        // Combine everything
        return HelpComponentCreator.createHelpComponent(
                leftHyphen
                        .append(middleText)
                        .append(rightHyphen)
                        .appendNewline()
                        .append(legend)
                        .appendNewline()
        );
    }
    
    @Override
    public @NotNull HelpComponent<VelocitySource, Component> getFooter(ExecutionContext<VelocitySource> context) {
        return HelpComponentCreator.createHelpComponent(
                Component.text("==============================")
                        .decorate(TextDecoration.STRIKETHROUGH, TextDecoration.BOLD)
                        .color(TextColor.fromHexString("#282863"))
        );
    }
    
    @Override
    public boolean isOptionEnabled(@NotNull Option option) {
        Object optionValue = optionValues.get(option);
        return optionValue instanceof Boolean
                && Objects.equals(optionValue, true);
    }
    
    @Override
    public @NotNull UsageFormatter<VelocitySource, Component> getUsageFormatter() {
        return usageFormatter;
    }
    
    public static class EcoUsageFormatter implements UsageFormatter<VelocitySource, Component> {
        
        @Override
        public @NotNull HelpComponent<VelocitySource, Component> format(
                Command<VelocitySource> lastOwningCommand,
                CommandUsage<VelocitySource> pathway,
                ExecutionContext<VelocitySource> context,
                HelpTheme<VelocitySource, Component> theme
        ) {
            String cmdPrefix = context.imperatConfig().commandPrefix();
            Component usageComponent = Component.text(cmdPrefix + context.command().format(), TextColor.fromHexString("#1f3b29"));
            for(CommandParameter<VelocitySource> parameter : pathway.getParameters()) {
                usageComponent = usageComponent
                        .appendSpace()
                        .append(formatParameter(context, parameter));
            }
            
            return HelpComponentCreator.createHelpComponent(usageComponent);
        }
        
        private Component formatParameter(ExecutionContext<VelocitySource> context, CommandParameter<VelocitySource> parameter) {
            var comp = Component.text(parameter.format());
            boolean hasParameterPermission = context.imperatConfig().getPermissionChecker().hasPermission(context.source(), parameter);
            if(!hasParameterPermission) {
                comp = comp.colorIfAbsent(NamedTextColor.RED);
                return comp;
            }
            
            if(parameter.isCommand()) {
                comp = comp.colorIfAbsent(TextColor.fromHexString("#459fa1"));
                comp = comp.shadowColor(ShadowColor.fromHexString("#210a57"));
            }else if(parameter.isOptional()) {
                comp = comp.colorIfAbsent(TextColor.fromHexString("#deb76a"));
            }else {
                comp = comp.colorIfAbsent(TextColor.fromHexString("#90d17d"));
            }
            
            if(!parameter.description().isEmpty()) {
                comp = comp.hoverEvent(
                        HoverEvent.showText(
                                Component.text(parameter.description().toString())
                                        .colorIfAbsent(NamedTextColor.GRAY)
                        )
                );
            }
            
            return comp;
        }
    }
    
}
