package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

import java.util.Optional;

@SubCommand(value = {"toggle", "t"})
@Description("Toggles guild chat ON/OFF")
public class ToggleSubCommand {
    
    @Dependency
    DominusGuilds plugin;

    @Usage
    public void toggle(VelocityPlayer source, @ContextResolved Guild<Player> sourceGuild){
        Optional<GuildMember<Player>> sourceMemberOptional = sourceGuild.getMember(source.uuid());
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