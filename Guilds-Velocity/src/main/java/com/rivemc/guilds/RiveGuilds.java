package com.rivemc.guilds;

import com.alessiodp.libby.VelocityLibraryManager;
import com.google.inject.Inject;
import com.rivemc.guilds.base.SimpleDistinctTagTracker;
import com.rivemc.guilds.base.SimpleGuildManager;
import com.rivemc.guilds.database.GuildStorage;
import com.rivemc.guilds.database.GuildStorageFactory;
import com.rivemc.guilds.listeners.ChatListener;
import com.rivemc.guilds.listeners.RegistryListener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(id = "rive-guilds", name = "RiveGuilds", version = "1.0.0", authors = {"Mqzen"})
@Getter
public final class RiveGuilds {
    
    private final Logger logger;
    private final ProxyServer server;
    private final Path dataDirectory;
    private ConfigurationNode config, dbConfig;
    
    private DistinctTagTracker tagTracker;
    private SimpleGuildManager guildManager;
    
    @Inject
    public RiveGuilds(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        
        setupDataDirectory();
    }
    
    private void setupDataDirectory() {
        try {
            if(!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
        } catch (IOException e) {
            logger.error("Failed to create plugin directory", e);
        }
    }
    
    private void initializeConfigurations() {
        loadMainConfig();
        loadDatabaseConfig();
    }
    
    private void loadMainConfig() {
        try {
            Path configPath = dataDirectory.resolve("config.yml");
            if (!Files.exists(configPath)) {
                copyDefaultConfig("config.yml", configPath);
            }
            
            ConfigurationLoader<CommentedConfigurationNode> loader = YamlConfigurationLoader.builder()
                    .path(configPath)
                    .build();
            config = loader.load();
        } catch (IOException e) {
            logger.error("Failed to load config.yml", e);
        }
    }
    
    //method to load "database.yml" just like config.yml.
    
    private void loadDatabaseConfig() {
        try {
            Path dbConfigPath = dataDirectory.resolve("database.yml");
            if (!Files.exists(dbConfigPath)) {
                copyDefaultConfig("database.yml", dbConfigPath);
            }
            
            ConfigurationLoader<CommentedConfigurationNode> loader = YamlConfigurationLoader.builder()
                    .path(dbConfigPath)
                    .build();
            dbConfig = loader.load();
            // Use dbConfig as needed
        } catch (IOException e) {
            logger.error("Failed to load database.yml", e);
        }
    }
    
    private void copyDefaultConfig(String fileName, Path configPath) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is != null) {
                Files.copy(is, configPath);
            }
        }
    }
    
    private void loadLibraries() {
        logger.info("Loading libraries...");
        VelocityLibraryManager<RiveGuilds> libraryManager = new VelocityLibraryManager<>(this, logger, dataDirectory, server.getPluginManager());
        libraryManager.addMavenCentral();
        libraryManager.addJitPack();
        RiveLibs.loadLibraries(libraryManager);
    }
    
    private void injectDependencies() {
        //TODO inject storage and do other stuff.
        //do database first
        GuildStorageFactory storageFactory = new GuildStorageFactory(this);
        try {
            GuildStorage<Player> storage = (GuildStorage<Player>) storageFactory.createStorage(dbConfig);
            this.tagTracker = new SimpleDistinctTagTracker();
            this.guildManager = new SimpleGuildManager(this, storage, tagTracker);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void registerEventListeners() {
        server.getEventManager().register(this, new ChatListener(this));
        server.getEventManager().register(this, new RegistryListener(this));
    }
    
    private void registerCommands() {
        
        CommandRegistrar registrar = new CommandRegistrar(this, server, logger);
        registrar.registerCommands();
    }
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            initializeConfigurations();
            loadLibraries();
            injectDependencies();
            registerEventListeners();
            registerCommands();
            logger.info("Enabled RiveGuilds v1.0.0-SNAPSHOT successfully!");
        } catch (Exception e) {
            logger.error("Failed to initialize RiveGuilds", e);
        }
    }
    
    // log method
    public void log(String message, Object... args) {
        logger.info(message, args);
    }
    
}
