package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.GuildRole;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.base.SimpleGuildMember;
import eg.mqzen.guilds.database.GuildUpdateAction;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.VelocitySource;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.Named;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@SubCommand("demote")
@Description("Demotes a member")
public class DemoteSubCommand {
    
    @Dependency
    DominusGuilds plugin;

    @Usage
    public void defaultUsage(VelocitySource source) {
        source.reply("Usage: /guild demote <player>");
    }

    @Usage
    public void demote(VelocitySource source, @Named("target") Player target, @ContextResolved Guild<Player> sourceGuild) {
        if (source == target) return;

        // Check if source is in a guild.
        
        // Check if target is in a guild.
        Optional<Guild<Player>> targetGuildOptional = plugin.getGuildManager().getPlayerGuild(target.getUniqueId());
        if (targetGuildOptional.isEmpty()) {
            source.reply("<red>" + target.getUsername() + " player isn't in a guild.");
            return;
        }

        Guild<Player> targetGuild = targetGuildOptional.get();

        // Check if target is in the same guild as source.
        if (sourceGuild != targetGuild) {
            source.reply("<red>You are not in the same guild as " + target.getUsername() + ".");
            return;
        }

        // Check if target is the owner of the guild
        if(sourceGuild.getOwner().getUUID().equals(target.getUniqueId())){
            source.reply("<red>You can't demote the owner.");
            return;
        }

        // Check if source has permissions
        Optional<GuildMember<Player>> sourceOptional = sourceGuild.getMember(source.uuid());
        Optional<GuildMember<Player>> targetOptional = sourceGuild.getMember(target.getUniqueId());
        if (sourceOptional.isEmpty() || targetOptional.isEmpty()) {
            source.reply("<red>Some-weird happened in the JVM/Server runtime, your guild suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        GuildMember<Player> sourceGuildMember = sourceOptional.get();
        GuildMember<Player> targetGuildMember = targetOptional.get();
        if (!sourceGuildMember.hasPermission(sourceGuild, GuildRole.Permission.DEMOTE_MEMBER)) {
            source.reply("<red>You don't have enough permissions to modify roles!");
            return;
        }

        GuildRole targetGuildRole = sourceGuild.getMemberRole(targetGuildMember);
        // Find the next lower role for the target member
        UUID newRoleUUID = null;

        List<GuildRole> guildRoles = sourceGuild.getRoles().stream()
                .sorted(Comparator.comparingInt((GuildRole r) -> r.getWeight()))
                .collect(Collectors.toList());
        for (GuildRole role : guildRoles) {
            if (role != targetGuildRole && role.getWeight() < targetGuildRole.getWeight()) {
                newRoleUUID = role.getID();
            }
        }

        // If new role uuid = null, then there's no role to be demoted to
        if (newRoleUUID == null) {
            source.reply("<red>" + target.getUsername() + " is already at the lowest role level.");
            return;
        }

        Optional<GuildRole> newRoleOptional = sourceGuild.getRoleByID(newRoleUUID);
        if (newRoleOptional.isEmpty()) {
            source.reply("<red>Some-weird happened in the JVM/Server runtime, queried role suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        // Demote the target guild member
        GuildRole newGuildRole = newRoleOptional.get();
        GuildMember<Player> newTargetMember = new SimpleGuildMember(
                target.getUniqueId(),
                target.getUsername(),
                newGuildRole.getID()
        );
        sourceGuild.removeMember(target.getUniqueId());
        sourceGuild.addMember(newTargetMember);

        plugin.getGuildManager().updateGuild(GuildUpdateAction.MEMBER_ROLE_DEMOTION, sourceGuild.getID(), sourceGuild, true)
                .sendErrorMessage(source.origin(), "Failed to demote " + target.getUsername() + ", Something went wrong! \n Contact an admin to resolve this matter.")
                .onSuccess((g) -> {
                    // Notify all guild members about the demotion
                    sourceGuild.getMembers().forEach(member -> {
                        Optional<Player> guildPlayerOpt = plugin.getServer().getPlayer(member.getUUID());
                        guildPlayerOpt.ifPresent(guildPlayer -> 
                            guildPlayer.sendRichMessage(sourceGuild.getTag().getMiniMessageFormattedTag()
                                    + " <red>" + target.getUsername() + " has been demoted to " + newGuildRole.getName() + ".")
                        );
                    });

                    Optional<Player> targetPlayerOpt = plugin.getServer().getPlayer(target.getUniqueId());
                    targetPlayerOpt.ifPresent(targetPlayer -> 
                        targetPlayer.sendRichMessage("<green>" + source.name() + " demoted you to " + newGuildRole.getName())
                    );
                });
    }
}