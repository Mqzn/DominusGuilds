package com.rivemc.guilds.database.storage;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.rivemc.guilds.Guild;
import com.rivemc.guilds.RiveGuilds;
import com.rivemc.guilds.database.BaseGuildStorage;
import com.rivemc.guilds.database.mongo.MongoDocumentGuildAdapter;
import com.rivemc.guilds.database.mongo.MongoDocumentObjectAdapter;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoGuildStorage extends BaseGuildStorage {

    public static final String MAIN_COLLECTION_NAME = "pavemc_guilds";

    private final MongoDocumentObjectAdapter<Guild<Player>> guildObjectAdapter;

    private MongoClient client;
    private MongoDatabase database;

    public MongoGuildStorage(RiveGuilds plugin, ConfigurationNode section) throws Exception {
        super(plugin, Type.MONGO, section);
        this.guildObjectAdapter = new MongoDocumentGuildAdapter(plugin);
    }
    private MongoClient createClient(ConfigurationNode section) {
        ConnectionString connectionString = new ConnectionString(section.node("connection-url").getString(""));
        return MongoClients.create(connectionString);
    }

    private MongoDatabase initializeDatabase(ConfigurationNode section) {
        String dbName = section.node("database").getString("");

        // Check/create collection once during initialization
        return client.getDatabase(dbName);
    }

    private boolean collectionExists(MongoDatabase db) {
        try (MongoCursor<String> cursor = db.listCollectionNames().cursor()) {
            while (cursor.hasNext()) {
                var next = cursor.next();
                if(next.equalsIgnoreCase(MAIN_COLLECTION_NAME)) {
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    protected void connect(ConfigurationNode section) {
        try {
            this.client = createClient(section);
            this.database = initializeDatabase(section);
            if (!collectionExists(this.database)) {
                this.database.createCollection(MAIN_COLLECTION_NAME);
            }
        }catch (Exception ex) {
            client = null;
            this.database = null;
            throw ex;
        }
    }

    @Override
    public void close() {
        if (client != null) {
            client.close(); // Gracefully shut down the connection pool
        }
    }

    @Override
    public CompletableFuture<Optional<Guild<Player>>> loadByName(@NotNull String guildName) {
        return CompletableFuture.supplyAsync(()-> {
            var collection = database.getCollection(MAIN_COLLECTION_NAME);
            var document = collection.find(Filters.eq("name",MAIN_COLLECTION_NAME)).first();
            if (document != null) {
                return Optional.of(guildObjectAdapter.fromDocument(document));
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Void> deleteByName(@NotNull String guildName) {
        return CompletableFuture.runAsync(() -> {
            var collection = database.getCollection(MAIN_COLLECTION_NAME);
            collection.deleteOne(Filters.eq("name", guildName));
        });
    }


    @Override
    public CompletableFuture<Guild<Player>> save(@NotNull Guild<Player> data) {
        return CompletableFuture.supplyAsync(() -> {
            var collection = database.getCollection(MAIN_COLLECTION_NAME);
            var document = guildObjectAdapter.toDocument(data);
            collection.replaceOne(Filters.eq("id", data.getID().toString()), document, new ReplaceOptions().upsert(true));
            return data;
        });
    }

    @Override
    public CompletableFuture<Optional<Guild<Player>>> load(@NotNull UUID guildId) {
        return CompletableFuture.supplyAsync(() -> {
            var collection = database.getCollection(MAIN_COLLECTION_NAME);
            var document = collection.find(Filters.eq("id", guildId.toString())).first();
            if (document != null) {
                return Optional.of(guildObjectAdapter.fromDocument(document));
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Void> delete(@NotNull UUID key) {
        return CompletableFuture.runAsync(() -> {
            var collection = database.getCollection(MAIN_COLLECTION_NAME);
            collection.deleteOne(Filters.eq("id", key.toString()));
        });
    }

    @Override
    public CompletableFuture<LinkedHashSet<Guild<Player>>> loadAll(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            var collection = database.getCollection(MAIN_COLLECTION_NAME);
            var documents = collection.find();
            if(limit > 0) {
                documents = documents.limit(limit);
            }

            LinkedHashSet<Guild<Player>> guilds = new LinkedHashSet<>();
            for (var document : documents) {
                guilds.add(guildObjectAdapter.fromDocument(document));
            }
            return guilds;
        });
    }


}
