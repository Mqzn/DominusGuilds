package com.rivemc.guilds.database.mongo;

import com.rivemc.guilds.*;
import com.rivemc.guilds.base.SimpleGuildOwnerInfo;
import com.rivemc.guilds.base.SimpleGuild;
import com.rivemc.guilds.base.SimpleGuildColor;
import com.rivemc.guilds.base.SimpleGuildMember;
import com.rivemc.guilds.base.SimpleGuildRole;
import com.rivemc.guilds.base.SimpleGuildTag;
import net.kyori.adventure.text.format.TextColor;
import org.bson.Document;
import com.velocitypowered.api.proxy.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Mqzen
 * @date 18/5/2025
 */
public final class MongoDocumentGuildAdapter implements MongoDocumentObjectAdapter<Guild<Player>> {

    private final MongoDocumentGuildTagAdapter tagAdapter = new MongoDocumentGuildTagAdapter();
    private final MongoDocumentGuildRoleAdapter roleAdapter = new MongoDocumentGuildRoleAdapter();
    private final MongoDocumentGuildMemberAdapter memberAdapter = new MongoDocumentGuildMemberAdapter();

    private final RiveGuilds plugin;
    public MongoDocumentGuildAdapter(RiveGuilds plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public Document toDocument(Guild<Player> guild) {
        Document document = new Document();
        document.put("id", guild.getID().toString());
        document.put("name", guild.getName());
        document.put("foundation-date", guild.getFoundationDate());
        document.put("owner", guild.getOwnerInfo().toString());
        document.put("tag", tagAdapter.toDocument((GuildTag) guild.getTag()));

        // Store roles as a sub-document instead of using dynamic keys
        Document rolesDoc = new Document();
        for(GuildRole role : guild.getRoles()) {
            rolesDoc.put(role.getID().toString(), roleAdapter.toDocument(role));
        }
        document.put("roles", rolesDoc);

        // Store members as a sub-document instead of using dynamic keys
        Document membersDoc = new Document();
        for(GuildMember<Player> member : guild.getMembers()) {
            membersDoc.put(member.getUUID().toString(), memberAdapter.toDocument(member));
        }
        document.put("members", membersDoc);

        return document;
    }

    @Override
    public Guild<Player> fromDocument(Document document) {
        UUID id = UUID.fromString(document.getString("id"));
        String name = document.getString("name");
        Date foundationDate = document.getDate("foundation-date");
        SimpleGuildOwnerInfo ownerInfo = SimpleGuildOwnerInfo.fromString(document.getString("owner"));
        GuildTag tag = tagAdapter.fromDocument((Document) document.get("tag"));

        Map<UUID, GuildRole> roles = new HashMap<>();
        Document rolesDoc = (Document) document.get("roles");
        if (rolesDoc != null) {
            for (String roleIdStr : rolesDoc.keySet()) {
                UUID roleId = UUID.fromString(roleIdStr);
                Document roleDoc = (Document) rolesDoc.get(roleIdStr);
                GuildRole role = roleAdapter.fromDocument(roleDoc);
                roles.put(roleId, role);
            }
        }

        Map<UUID, GuildMember<Player>> members = new HashMap<>();
        Document membersDoc = (Document) document.get("members");
        if (membersDoc != null) {
            for (String memberIdStr : membersDoc.keySet()) {
                UUID memberId = UUID.fromString(memberIdStr);
                Document memberDoc = (Document) membersDoc.get(memberIdStr);
                GuildMember<Player> member = memberAdapter.fromDocument(memberDoc);
                members.put(memberId, member);
            }
        }

        return new SimpleGuild(plugin, id, name, foundationDate, tag, ownerInfo, members, roles);
    }

    static class MongoDocumentGuildTagAdapter implements MongoDocumentObjectAdapter<GuildTag> {
        @Override
        public Document toDocument(GuildTag tag) {
            Document document = new Document();
            document.put("value", tag.getPlainValue());
            document.put("color", tag.getColor().getName());
            return document;
        }

        @Override
        public GuildTag fromDocument(Document document) {
            String value = document.getString("value");
            String color = document.getString("color");
            return new SimpleGuildTag(value, SimpleGuildColor.valueOf(color));
        }
    }

    static class MongoDocumentGuildMemberAdapter implements MongoDocumentObjectAdapter<GuildMember<Player>> {
        public MongoDocumentGuildMemberAdapter() {
        }

        @Override
        public Document toDocument(GuildMember<Player> member) {
            Document document = new Document();
            document.put("uuid", member.getUUID().toString());
            document.put("name", member.getName());
            document.put("role-id", member.getRoleId().toString()); // Convert to string
            return document;
        }

        @Override
        public GuildMember<Player> fromDocument(Document document) {
            UUID uuid = UUID.fromString(document.getString("uuid"));
            String name = document.getString("name");
            UUID roleId = UUID.fromString(document.getString("role-id"));
            return new SimpleGuildMember(uuid, name, roleId);
        }
    }

    static class MongoDocumentGuildRoleAdapter implements MongoDocumentObjectAdapter<GuildRole> {
        @Override
        public Document toDocument(GuildRole role) {
            Document document = new Document();
            document.put("id", role.getID().toString());
            document.put("name", role.getName());
            document.put("prefix", role.getPrefix());
            document.put("weight", role.getWeight());
            document.put("permissions", role.getPermissions().stream().map(GuildRole.Permission::getValue).toList());
            return document;
        }

        @Override
        public GuildRole fromDocument(Document document) {
            UUID id = UUID.fromString(document.getString("id"));
            String name = document.getString("name");
            String prefix = document.getString("prefix");
            int weight = document.getInteger("weight");
            List<String> permissions = document.getList("permissions", String.class);
            Map<String, GuildRole.Permission> permissionsMap = new HashMap<>();
            if (permissions != null) {
                for(String permission : permissions) {
                    permissionsMap.put(permission, GuildRole.Permission.from(permission));
                }
            }

            return new SimpleGuildRole(id, name, prefix, weight, permissionsMap);
        }
    }
}