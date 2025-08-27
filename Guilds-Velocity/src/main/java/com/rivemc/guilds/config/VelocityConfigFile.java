package com.rivemc.guilds.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;

@Singleton
public class VelocityConfigFile implements Config<ConfigurationNode> {
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode root;

    @Inject
    public VelocityConfigFile(ConfigurationLoader<CommentedConfigurationNode> loader) throws IOException {
        this.loader = loader;
        reload();
    }

    @Override
    public ConfigurationNode getRoot() {
        return root;
    }

    @Override
    public void reload() throws IOException {
        root = loader.load();
    }

    @Override
    public void save() throws IOException {
        loader.save(root);
    }
}
