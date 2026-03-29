package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.types.Context;
import studio.mevera.imperat.annotations.types.Dependency;
import studio.mevera.imperat.annotations.types.Description;
import studio.mevera.imperat.annotations.types.SubCommand;
import studio.mevera.imperat.annotations.types.Execute;

import java.util.Optional;

@SubCommand(value = {"toggle", "t"})
@Description("Toggles guild chat ON/OFF")
public class ToggleSubCommand {
    
    @Dependency
    DominusGuilds plugin;

    @Execute
    public void toggle(VelocityPlayer source, @Context Guild<Player> sourceGuild){
        Optional<GuildMember<Player>> sourceMemberOptional = sourceGuild.getMember(source.asPlayer().getUniqueId());
        if (sourceMemberOptional.isEmpty()) {
            source.reply("<red>Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        GuildMember<Player> sourceGuildMember = sourceMemberOptional.get();
        sourceGuildMember.toggleGuildChat();
        if (sourceGuildMember.hasChatToggled())
            source.reply("<green>You have toggled guild chat <dark_green>ON");
        else
            source.reply("<green>You have toggled guild chat <red>OFF");
    }
}