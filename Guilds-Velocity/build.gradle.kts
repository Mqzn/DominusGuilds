import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.compile.JavaCompile

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.2"
}

group = "com.rivemc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "central-snapshots"
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }
    //add paper mc repo
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    //add oss repo for velocity



    maven {
        name = "libby-repo"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }

}

dependencies {
    implementation(project(":Guilds-API"))
    implementation("org.spongepowered:configurate-yaml:4.2.0")

    compileOnly("com.google.inject:guice:7.0.0")

    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT") {
        this.isTransitive = true
    }

    implementation("com.github.ben-manes.caffeine:caffeine:3.2.2")

    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")// The Velocity API with no shadowing. Requires the OSS repo.

    //TODO add dependency runtime using libby for other dependencies
    implementation("com.alessiodp.libby:libby-velocity:2.0.0-SNAPSHOT")

    compileOnly("net.kyori:adventure-platform-velocity:4.3.4")
    compileOnly("net.kyori:adventure-text-minimessage:4.19.0")
    compileOnly("studio.mevera:imperat-core:2.1.0-SNAPSHOT")
    compileOnly("studio.mevera:imperat-velocity:2.1.0-SNAPSHOT")

    compileOnly("org.mongodb:mongodb-driver-sync:5.3.1")

    compileOnly ("org.redisson:redisson:3.48.0")

    compileOnly("me.clip:placeholderapi:2.11.6")

    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named<ShadowJar>("shadowJar") {
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    archiveBaseName.set("Pave-Guilds-Velocity")
    archiveClassifier.set("")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    destinationDirectory.set(file(project.rootProject.property("shadowJarOutputDir") as String))

    relocate("studio.mevera", "com.rivemc.guilds.libs.studio.mevera")
    relocate("com.mongodb", "com.rivemc.guilds.libs.com.mongodb")
    relocate("org.bson", "com.rivemc.guilds.libs.org.bson")
    relocate("net.kyori", "com.rivemc.guilds.libs.net.kyori")
    relocate("org.redisson", "com.rivemc.guilds.libs.org.redisson")
    relocate("com.github.benmanes.caffeine", "com.rivemc.guilds.libs.com.github.benmanes.caffeine")
}
