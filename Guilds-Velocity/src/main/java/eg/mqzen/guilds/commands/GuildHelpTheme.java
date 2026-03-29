package eg.mqzen.guilds.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import studio.mevera.imperat.VelocityCommandSource;
import studio.mevera.imperat.command.Command;
import studio.mevera.imperat.command.CommandPathway;
import studio.mevera.imperat.command.arguments.Argument;
import studio.mevera.imperat.command.tree.help.renderers.UsageFormatter;
import studio.mevera.imperat.command.tree.help.theme.BaseHelpTheme;
import studio.mevera.imperat.command.tree.help.theme.HelpComponent;
import studio.mevera.imperat.command.tree.help.theme.HelpTheme;
import studio.mevera.imperat.context.ExecutionContext;

public class GuildHelpTheme extends BaseHelpTheme<VelocityCommandSource, Component> {
    
    private final UsageFormatter<VelocityCommandSource, Component> usageFormatter = new GuildUsageFormatter();
    
    public GuildHelpTheme() {
        super(
            HelpComponentCreator::createHelpComponent
        );
    }
    
    @Override
    public @NotNull Component createEmptyContent() {
        return Component.empty();
    }
    
    @Override
    public @NotNull Component getHeaderContent(ExecutionContext<VelocityCommandSource> context) {
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
    public @NotNull Component getFooterContent(ExecutionContext<VelocityCommandSource> context) {
        return Component.text("==============================")
                .decorate(TextDecoration.STRIKETHROUGH, TextDecoration.BOLD)
                .color(TextColor.fromHexString("#282863"));
    }
    
    @Override
    public @NotNull UsageFormatter<VelocityCommandSource, Component> getUsageFormatter() {
        return usageFormatter;
    }
    public static class GuildUsageFormatter implements UsageFormatter<VelocityCommandSource, Component> {
        
        @Override
        public @NotNull HelpComponent<VelocityCommandSource, Component> format(
                Command<VelocityCommandSource> lastOwningCommand,
                CommandPathway<VelocityCommandSource> pathway,
                ExecutionContext<VelocityCommandSource> context,
                HelpTheme<VelocityCommandSource, Component> theme
        ) {
            String cmdPrefix = context.imperatConfig().commandPrefix();
            Component usageComponent = Component.text(cmdPrefix + context.command().format(), TextColor.fromHexString("#1f3b29"));
            for(Argument<VelocityCommandSource> parameter : pathway.getArguments()) {
                usageComponent = usageComponent
                        .appendSpace()
                        .append(formatParameter(context, parameter));
            }
            
            return HelpComponentCreator.createHelpComponent(usageComponent);
        }
        
        private Component formatParameter(ExecutionContext<VelocityCommandSource> context, Argument<VelocityCommandSource> parameter) {
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
            
            if(!parameter.getDescription().isEmpty()) {
                comp = comp.hoverEvent(
                        HoverEvent.showText(
                                Component.text(parameter.getDescription().toString())
                                        .colorIfAbsent(NamedTextColor.GRAY)
                        )
                );
            }
            
            return comp;
        }
    }
    
}
