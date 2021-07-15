import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
    implementation("cloud.commandframework:cloud-core:1.5.0")
    implementation("cloud.commandframework:cloud-paper:1.5.0")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.5.0")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.0")
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
}

group = "com.lokkeestudios"
version = "0.1.0-SNAPSHOT"
description = "Skylands is an upcoming open-source System, with all sorts of RPG related functionalities."
java.sourceCompatibility = JavaVersion.VERSION_16

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<ShadowJar> {
        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        filesNotMatching("**/*.zip") {
            expand("pluginVersion" to version)
        }
    }
}
