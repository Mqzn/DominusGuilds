package eg.mqzen.guilds.database;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Storage<K, D> {

    //CRUD
    //create/write/save read update deleteFromStorage
    Type type();
    CompletableFuture<D> save(@NotNull D data);
    CompletableFuture<Optional<D>> load(@NotNull final K key);
    CompletableFuture<Void> delete(@NotNull K key);
    CompletableFuture<LinkedHashSet<D>> loadAll(int limit);
    void close();

    enum Type {

        MONGO,

        MY_SQL,

        SQLITE,

        EMPTY;

    }
}
