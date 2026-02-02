package eg.mqzen.guilds.commands;

//annotation for classes only
import eg.mqzen.guilds.GuildRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredGuildPermissions {

    GuildRole.Permission[] value();
}
