package eg.mqzen.guilds.commands.guildsubs;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.GuildMember;
import eg.mqzen.guilds.GuildRole;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.base.SimpleGuildMember;
import eg.mqzen.guilds.commands.VelocityPlayer;
import eg.mqzen.guilds.database.GuildUpdateAction;
import com.velocitypowered.api.proxy.Player;
import studio.mevera.imperat.annotations.ContextResolved;
import studio.mevera.imperat.annotations.Dependency;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.Named;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@SubCommand("promote")
@Description("Promotes a player in your guild")
public class PromoteSubCommand {
    
    @Dependency
    DominusGuilds plugin;

    @Usage
    public void defaultUsage(VelocityPlayer source) {
        source.reply("Usage: /guild promote <player>");
    }

    @Usage
    public void promote(VelocityPlayer source, @Named("target") Player target, @ContextResolved Guild<Player> sourceGuild) {
        if (source == target) return;

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
        if (!sourceGuildMember.hasPermission(sourceGuild, GuildRole.Permission.PROMOTE_MEMBER)) {
            source.reply("<red>You don't have enough permissions to modify roles!");
            return;
        }

        GuildRole targetGuildRole = sourceGuild.getMemberRole(targetGuildMember);
        // Find the next higher role for the target member
        UUID newRoleUUID = null;

        List<GuildRole> guildRoles = sourceGuild.getRoles().stream()
                .sorted((r1, r2) -> Integer.compare(r2.getWeight(), r1.getWeight()))
                .collect(Collectors.toList());
        for (GuildRole role : guildRoles) {
            if (!role.getID().equals(targetGuildRole.getID()) &&
                    !role.getID().equals(sourceGuild.getMemberRole(sourceGuild.getOwner()).getID()) &&
                    role.getWeight() > targetGuildRole.getWeight()) {
                newRoleUUID = role.getID();
            }
        }

        // If new role uuid = null, then there's no role to be promoted to
        if (newRoleUUID == null) {
            source.reply("<red>" + target.getUsername() + " is already at the highest role level.");
            source.reply("<red>If you wanna transfer the guild, use /guild transfer <player>");
            return;
        }

        Optional<GuildRole> newRoleOptional = sourceGuild.getRoleByID(newRoleUUID);
        if (newRoleOptional.isEmpty()) {
            source.reply("<red>Some-weird happened in the JVM/Server runtime, queried role suddenly disappeared from existence!");
            source.reply("<red>Please contact an admin to resolve this matter.");
            return;
        }

        // Promote the target guild member
        GuildRole newGuildRole = newRoleOptional.get();
        GuildMember<Player> newTargetMember = new SimpleGuildMember(
                target.getUniqueId(),
                target.getUsername(),
                newGuildRole.getID()
        );
        sourceGuild.removeMember(target.getUniqueId());
        sourceGuild.addMember(newTargetMember);

        plugin.getGuildManager().updateGuild(
                  GuildUpdateAction.MEMBER_ROLE_PROMOTION, sourceGuild.getID(),
                  sourceGuild, true
                )
                .sendErrorMessage(source.origin(), "Failed to promote " + target.getUsername() + ". Please try again later.")
                .onSuccess(()-> {
                    source.reply("<green>You promoted " + target.getUsername() + " to " + newGuildRole.getName());
                    Optional<Player> targetPlayerOpt = plugin.getServer().getPlayer(target.getUniqueId());
                    targetPlayerOpt.ifPresent(targetPlayer -> 
                        targetPlayer.sendRichMessage("<green>" + source.name() + " promoted you to " + newGuildRole.getName())
                    );
                });
    }
}