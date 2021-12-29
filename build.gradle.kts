plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.papermc.paperweight.userdev") version "1.3.3"
}

group = "com.lokkeestudios"
version = "0.1.0-SNAPSHOT"
description = "Skylands is an upcoming open-source System, with all sorts of RPG related functionalities."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
    implementation("cloud.commandframework:cloud-core:1.6.1")
    implementation("cloud.commandframework:cloud-paper:1.6.1")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.6.1")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.3")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    processResources {
        filesNotMatching("**/*.zip") {
            expand("pluginVersion" to version)
        }
    }
}
