import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.2"

}

group = "eg.mqzen"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    //add paper mc repo
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }


    maven {
        name = "alessiodpRepoSnapshots"
        url = uri("https://repo.alessiodp.com/snapshots")
    }

}

dependencies {
    implementation(project(":Guilds-API"))
    implementation("com.esotericsoftware:kryo:5.6.2")
    implementation("org.spongepowered:configurate-yaml:4.2.0")

    compileOnly("com.google.inject:guice:7.0.0")

    compileOnly("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")

    compileOnly("studio.mevera:imperat-core:3.1.0")
    compileOnly("studio.mevera:imperat-velocity:3.1.0")

    compileOnly("dev.dejvokep:boosted-yaml:1.3.6")
    compileOnly("dev.dejvokep:boosted-yaml-spigot:1.5")

    compileOnly("org.mongodb:mongodb-driver-sync:5.3.1")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    //TODO add dependency runtime using libby for other dependencies
    implementation("com.alessiodp.libby:libby-velocity:2.0.0-SNAPSHOT")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.2")
    
    // Redis for cross-server communication
    implementation("redis.clients:jedis:5.1.2")

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named<ShadowJar>("shadowJar") {
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    archiveBaseName.set("DominusGuilds")
    archiveClassifier.set("")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    destinationDirectory.set(file(project.rootProject.property("shadowJarOutputDir") as String))

    relocate("studio.mevera", "eg.mqzen.guilds.libs.studio.mevera")
    relocate("com.mongodb", "eg.mqzen.guilds.libs.com.mongodb")
    relocate("org.bson", "eg.mqzen.guilds.libs.org.bson")
    //relocate("net.kyori", "eg.mqzen.guilds.libs.net.kyori")
    relocate("com.github.benmanes.caffeine", "eg.mqzen.guilds.libs.com.github.benmanes.caffeine")
    relocate("redis.clients.jedis", "eg.mqzen.guilds.libs.redis.clients.jedis")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("plugin.yml") {
        expand(
            "version" to project.version
        )
    }
}