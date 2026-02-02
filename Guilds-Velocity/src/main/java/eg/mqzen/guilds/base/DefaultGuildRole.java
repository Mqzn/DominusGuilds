package eg.mqzen.guilds.base;

import eg.mqzen.guilds.GuildRole;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Mqzen
 * @date 18/5/2025
 */
public interface DefaultGuildRole {

    GuildRole MEMBER = new SimpleGuildRole("member", "Member", 0, Collections.emptySet());

    GuildRole MODERATOR = new SimpleGuildRole("mod", "Moderator", 1, Set.of(GuildRole.Permission.INVITE_OUTSIDERS));

    GuildRole ADMIN = new SimpleGuildRole("admin", "Admin", 2, Set.of(GuildRole.Permission.INVITE_OUTSIDERS, GuildRole.Permission.KICK_MEMBER, GuildRole.Permission.PROMOTE_MEMBER, GuildRole.Permission.DEMOTE_MEMBER));

    GuildRole LEADER = new SimpleGuildRole("leader", "Leader", 100, GuildRole.Permission.ALL);

    Map<UUID, GuildRole> ALL = Map.of(
            MEMBER.getID(), MEMBER,
            MODERATOR.getID(), MODERATOR,
            ADMIN.getID(), ADMIN,
            LEADER.getID(), LEADER
    );
}
