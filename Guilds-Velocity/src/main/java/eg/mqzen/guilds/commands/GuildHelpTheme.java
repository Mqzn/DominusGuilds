package eg.mqzen.guilds.commands;

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
import studio.mevera.imperat.command.tree.help.renderers.UsageFormatter;
import studio.mevera.imperat.command.tree.help.theme.BaseHelpTheme;
import studio.mevera.imperat.command.tree.help.theme.HelpComponent;
import studio.mevera.imperat.command.tree.help.theme.HelpTheme;
import studio.mevera.imperat.context.ExecutionContext;

public class GuildHelpTheme extends BaseHelpTheme<VelocitySource, Component> {
    
    private final UsageFormatter<VelocitySource, Component> usageFormatter = new GuildUsageFormatter();
    
    public GuildHelpTheme() {
        super(
            PresentationStyle.FLAT,
            1,
            HelpComponentCreator::createHelpComponent
        );
    }
    
    @Override
    public @NotNull Component createEmptyContent() {
        return Component.empty();
    }
    
    @Override
    public @NotNull Component getBranchContent() {
        return Component.text("├─ ");
    }
    
    @Override
    public @NotNull Component getLastBranchContent() {
        return Component.text("└─ ");
    }
    
    @Override
    public @NotNull Component getIndentContent() {
        return Component.text("│  ");
    }
    
    @Override
    public @NotNull Component getEmptyIndentContent() {
        return Component.text("   ");
    }
    
    @Override
    public @NotNull Component getHeaderContent(ExecutionContext<VelocitySource> context) {
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
        return leftHyphen
                .append(middleText)
                .append(rightHyphen)
                .appendNewline()
                .append(legend)
                .appendNewline();
    }
    
    @Override
    public @NotNull Component getFooterContent(ExecutionContext<VelocitySource> context) {
        return Component.text("==============================")
                .decorate(TextDecoration.STRIKETHROUGH, TextDecoration.BOLD)
                .color(TextColor.fromHexString("#282863"));
    }
    
    @Override
    public @NotNull UsageFormatter<VelocitySource, Component> getUsageFormatter() {
        return usageFormatter;
    }
    public static class GuildUsageFormatter implements UsageFormatter<VelocitySource, Component> {
        
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
                comp = comp.shadowColor(ShadowColor.fromHexString("#210a5700"));
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
