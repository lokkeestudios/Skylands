import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.3.3"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.6.10"
}

group = "com.lokkeestudios"
version = "0.1.0-SNAPSHOT"
description = "Skylands is an upcoming open-source System, with all sorts of RPG related functionalities."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
    implementation("cloud.commandframework:cloud-core:1.6.1")
    implementation("cloud.commandframework:cloud-paper:1.6.1")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.6.1")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.4")
    implementation("com.github.retrooper:packetevents:spigot-SNAPSHOT")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

bukkit {
    name = "Skylands"
    version = "0.1.0-SNAPSHOT"
    main = "com.lokkeestudios.skylands.Skylands"
    apiVersion = "1.18"
    authors = listOf("LOKKEE")
    description = "Skylands is an upcoming open-source System, with all sorts of RPG related functionalities."
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
}
