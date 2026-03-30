plugins {
    id("java")
}

group = "eg.mqzen"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    // Spigot API
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    // For other potential repositories
    maven {
        name = "sonatype-oss-releases"
        url = uri("https://oss.sonatype.org/content/repositories/releases/")
    }
    // Maven Local
    mavenLocal()
}

dependencies {
    implementation(project(":Guilds-API"))
    implementation("com.esotericsoftware:kryo:5.6.2")

    // Spigot API (version should match your network)
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    
    // Jedis for Redis
    implementation("redis.clients:jedis:5.1.2")
    
//    // TriumphGUI for GUIs
    //TODO use lotus from mevera
//    implementation("github.olydev:TriumphGUI:1.11.0")
    
    // Adventure API (usually bundled with Spigot, but explicit for clarity)
    implementation("net.kyori:adventure-bukkit:4.13.0")
    
    // Gson for JSON serialization (matching what Velocity likely uses based on plan)
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Guava for caching (used in GuildCache)
    implementation("com.google.guava:guava:32.1.2-jre")
    
    // Lombok for cleaner code
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Process resources to handle version substitution if needed
tasks.processResources {
    inputs.property("version", project.version)
    
    filesMatching("plugin.yml") {
        expand(
            "version" to project.version
        )
    }
}