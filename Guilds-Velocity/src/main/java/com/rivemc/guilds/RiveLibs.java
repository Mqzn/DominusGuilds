package com.rivemc.guilds;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.Repositories;
import com.alessiodp.libby.VelocityLibraryManager;

public interface RiveLibs {

    String MAVEN_CENTRAL_SNAPSHOTS = "https://central.sonatype.com/repository/maven-snapshots/";
    
    Library ADVENTURE_BUKKIT = Library.builder()
        .repository(Repositories.MAVEN_CENTRAL)
        .groupId("net{}kyori") // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
        .artifactId("adventure-platform-bukkit")
        .version("4.3.4")
        .resolveTransitiveDependencies(true)
        .relocate("net{}kyori", "com{}rivemc{}guilds{}libs{}net{}kyori")
        // Relocation is applied to the downloaded jar before loading it
        .build();


    Library ADVENTURE_MINI_MESSAGE = Library.builder()
        .repository(Repositories.MAVEN_CENTRAL)
        .groupId("net{}kyori") // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
        .artifactId("adventure-text-minimessage")
        .version("4.19.0")
        .relocate("net{}kyori", "com{}rivemc{}guilds{}libs{}net{}kyori")
        .resolveTransitiveDependencies(true)
        // Relocation is applied to the downloaded jar before loading it
        .build();

    Library IMPERAT_CORE = Library.builder()
        .repository(MAVEN_CENTRAL_SNAPSHOTS)
        .groupId("studio{}mevera")
        .artifactId("imperat-core")
        .version("2.1.0-SNAPSHOT")
        .resolveTransitiveDependencies(true)
        .relocate("studio{}mevera", "com{}rivemc{}guilds{}libs{}studio{}mevera")
        .excludeTransitiveDependency("net{}kyori", "adventure-text-minimessage")
        .excludeTransitiveDependency("net{}kyori", "adventure-platform-bukkit")
        .build();

    Library IMPERAT_VELOCITY = Library.builder()
        .repository(MAVEN_CENTRAL_SNAPSHOTS)
        .groupId("studio{}mevera")
        .artifactId("imperat-velocity")
        .version("2.1.0-SNAPSHOT")
        .resolveTransitiveDependencies(true)
        .relocate("studio{}mevera", "com{}rivemc{}guilds{}libs{}studio{}mevera") //com.rivemc.guilds.libs.dev.velix
        .relocate("net{}kyori", "com{}rivemc{}guilds{}libs{}net{}kyori")
        .excludeTransitiveDependency("net{}kyori", "adventure-text-minimessage")
        .excludeTransitiveDependency("net{}kyori", "adventure-platform-bukkit")
        .build();

    Library MONGODB_DRIVER = Library.builder()
        .repository(Repositories.MAVEN_CENTRAL)
        .groupId("org{}mongodb")
        .artifactId("mongodb-driver-sync")
        .version("5.3.1")
        .resolveTransitiveDependencies(true)
        .relocate("com{}mongodb", "com{}rivemc{}guilds{}libs{}com{}mongodb")
        .relocate("org{}bson", "com{}rivemc{}guilds{}libs{}org{}bson")
        .build();

    /*Library JACKSON = Library.builder()
            .groupId("com{}fasterxml{}jackson{}core")
            .artifactId("jackson-databind")
            .version("2.18.2")
            .relocate("com{}fasterxml", "com{}rivemc{}guilds{}libs{}com{}fasterxml")
            .build();

    Library REDISSON = Library.builder()
        .repository(Repositories.MAVEN_CENTRAL)
        .groupId("org{}redisson")
        .artifactId("redisson")
        .version("3.49.0")
        .resolveTransitiveDependencies(true)
        .excludeTransitiveDependency("com{}fasterxml{}jackson{}core", "jackson-databind")
        .excludeTransitiveDependency("com{}fasterxml{}jackson{}core", "jackson-core")
        .relocate("org{}redisson", "com{}rivemc{}guilds{}libs{}org{}redisson")
        .relocate("io{}netty", "com{}rivemc{}guilds{}libs{}io{}netty")
        .build();
    */
    Library[] ALL = new Library[] {
        IMPERAT_CORE,
        IMPERAT_VELOCITY,
        MONGODB_DRIVER,
        //REDISSON
        //CAFFEINE
    };


    static void loadLibraries(VelocityLibraryManager<RiveGuilds> libraryManager) {
        libraryManager.loadLibraries(ALL);
    }

}