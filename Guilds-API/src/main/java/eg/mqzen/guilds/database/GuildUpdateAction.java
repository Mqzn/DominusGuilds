package eg.mqzen.guilds.database;

public enum GuildUpdateAction {

    CREATE_GUILD,

    DISBAND_GUILD,

    CHANGE_NAME,

    CHANGE_TAG,

    CHANGE_MOTD,

    RESET_MOTD,

    ADD_MEMBER,

    REMOVE_MEMBER,

    MEMBER_ROLE_PROMOTION,

    MEMBER_ROLE_DEMOTION,

    MEMBER_ROLE_SET,

    CHANGE_OWNERSHIP,

    UNKNOWN; // Used for unknown actions, should not be used in practice


    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    public boolean isCreateGuild() {
        return this == CREATE_GUILD;
    }

    public boolean isDisbandGuild() {
        return this == DISBAND_GUILD;
    }

    public boolean isUpdate() {
        return this != CREATE_GUILD && this != DISBAND_GUILD && this != UNKNOWN;
    }

}

