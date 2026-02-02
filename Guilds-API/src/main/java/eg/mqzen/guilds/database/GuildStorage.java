package eg.mqzen.guilds.database;

import eg.mqzen.guilds.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GuildStorage<P> extends Storage<UUID, Guild<P>> {

    CompletableFuture<Optional<Guild<P>>> loadByName(@NotNull final String guildName);
    CompletableFuture<Void> deleteByName(@NotNull String guildName);
}
