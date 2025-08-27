package com.rivemc.guilds.commands.guildsubs;

import com.rivemc.guilds.commands.VelocityPlayer;
import studio.mevera.imperat.annotations.*;
import com.rivemc.guilds.Guild;
import com.rivemc.guilds.GuildMember;
import com.rivemc.guilds.RiveGuilds;
import com.velocitypowered.api.proxy.Player;

import java.util.Optional;

@SubCommand(value = {"toggle", "t"})
@Description("Toggles guild chat ON/OFF")
public class ToggleSubCommand {
    
    @Dependency
    RiveGuilds plugin;

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