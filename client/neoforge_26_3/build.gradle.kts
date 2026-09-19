plugins {
    id("dev.architectury.loom-no-remap") version Versions.architecturyLoom
//    id("architectury-plugin") version "3.5-SNAPSHOT"
}

java.sourceCompatibility = JavaVersion.VERSION_25
java.targetCompatibility = JavaVersion.VERSION_25

//architectury {
//  platformSetupLoomIde()
//  neoForge()
//}

repositories {
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:26.3")
    neoForge("net.neoforged:neoforge:26.3.0.6-beta")

    compileOnly("de.maxhenkel.voicechat:voicechat-api:2.6.0")
}

tasks {
    processResources {
        filesMatching("META-INF/neoforge.mods.toml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        archiveFileName.set("[neoforge-26.3]AllMusic_Client-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))
    }

    build {
        dependsOn(shadowJar)
    }
}
