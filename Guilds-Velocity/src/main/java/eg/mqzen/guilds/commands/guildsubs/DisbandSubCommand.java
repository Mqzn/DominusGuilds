package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.commands.VelocityPlayer;
import com.velocitypowered.api.proxy.Player;

import studio.mevera.imperat.annotations.types.Context;
import studio.mevera.imperat.annotations.types.Dependency;
import studio.mevera.imperat.annotations.types.Description;
import studio.mevera.imperat.annotations.types.Execute;
import studio.mevera.imperat.annotations.types.SubCommand;

import java.util.Optional;

@SubCommand("disband")
@Description("Disband your guild (owner only)")
public class DisbandSubCommand {

    @Dependency
    DominusGuilds plugin;

    @Execute
    public void disband(VelocityPlayer source, @Context Guild<Player> sourceGuild) {
        Optional<GuildMember<Player>> guildMemberOptional = sourceGuild.getMember(source.asPlayer().getUniqueId());
        if (guildMemberOptional.isEmpty()) {
            source.reply("<red>Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        GuildMember<Player> guildMember = guildMemberOptional.get();

        // Owner-only (leader-only)
        if (!guildMember.equals(sourceGuild.getOwner())) {
            source.reply("<red>Only the guild leader can disband the guild.");
            source.reply("<gray>If you just want to leave, use <yellow>/guild leave</yellow>.");
            return;
        }

        String guildName = sourceGuild.getName();
        String guildTag = sourceGuild.getTag().getMiniMessageFormattedTag();

        // Notify members first (best-effort)


        plugin.getGuildManager().deleteGuild(sourceGuild)
                .sendErrorMessage(source.origin(), "<red>Failed to disband guild '" + guildName + "'.")
                .onSuccess(() -> {
                    sourceGuild.getMembers().forEach(member -> {
                        plugin.getServer().getPlayer(member.getUUID())
                                .ifPresent(p -> p.sendRichMessage(guildTag + " <red>The guild has been disbanded by " + source.name() + "."));
                    });
                    source.reply("<green>Successfully disbanded guild '" + guildName + "'.");
                });
    }
}
