package eg.mqzen.guilds.database;


import eg.mqzen.guilds.DominusGuilds;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurationNode;

/**
 * @author Mqzen
 * @date 18/5/2025
 */
public abstract class BaseGuildStorage implements GuildStorage<Player> {

    @Getter
    private final DominusGuilds plugin;
    private final Type type;
    protected ConfigurationNode section;

    protected BaseGuildStorage(DominusGuilds plugin, Type type, ConfigurationNode section) throws Exception {
        this.plugin = plugin;
        this.section = section;
        this.type = type;
        connect(section);
    }

    @Override
    public Type type() {
        return type;
    }

    protected abstract void connect(ConfigurationNode section) throws Exception;

}