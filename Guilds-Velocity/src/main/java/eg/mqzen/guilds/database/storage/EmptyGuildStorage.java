package eg.mqzen.guilds.database.storage;

import eg.mqzen.guilds.Guild;
import eg.mqzen.guilds.DominusGuilds;
import eg.mqzen.guilds.database.BaseGuildStorage;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EmptyGuildStorage extends BaseGuildStorage {

    
    public EmptyGuildStorage(DominusGuilds plugin) throws Exception {
        super(plugin, Type.EMPTY, null);
    }

    @Override
    protected void connect(ConfigurationNode section) throws Exception {

    }

    @Override
    public CompletableFuture<Optional<Guild<Player>>> loadByName(@NotNull String guildName) {
        return null;
    }

    @Override public
    CompletableFuture<Void> deleteByName(@NotNull String guildName) {
        return null;
    }

    @Override
    public CompletableFuture<Guild<Player>> save(@NotNull Guild<Player> data) {
        return CompletableFuture.supplyAsync(()-> null);
    }

    @Override
    public CompletableFuture<Optional<Guild<Player>>> load(@NotNull UUID key) {
        return CompletableFuture.supplyAsync(Optional::empty);
    }

    @Override
    public CompletableFuture<Void> delete(@NotNull UUID key) {
        return CompletableFuture.runAsync(()-> {});
    }

    @Override
    public CompletableFuture<LinkedHashSet<Guild<Player>>> loadAll(int limit) {
        return CompletableFuture.supplyAsync(()-> limit <= 0 ? new LinkedHashSet<>() : new LinkedHashSet<>(limit));
    }

    @Override
    public void close() {

    }
}
