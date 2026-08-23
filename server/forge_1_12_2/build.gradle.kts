import com.gtnewhorizons.retrofuturagradle.mcp.ReobfuscatedJar

plugins {
    id("com.gtnewhorizons.retrofuturagradle") version "2.0.2"
}

minecraft {
    mcVersion.set("1.12.2")

    mcpMappingChannel.set("stable")
    mcpMappingVersion.set("39")
}

repositories {
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    shadowImplementation("net.kyori:adventure-text-minimessage:4.26.1")
    shadowImplementation("net.kyori:adventure-api:4.26.1")
    shadowImplementation("net.kyori:adventure-text-serializer-gson:4.8.1")
    shadowImplementation("net.kyori:adventure-text-serializer-legacy:4.8.1")
    shadowImplementation("net.kyori:adventure-text-serializer-plain:4.8.1")
    shadowImplementation("net.kyori:adventure-key:4.8.1")
}

tasks {
    processResources {
        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        archiveFileName.set("[forge-1.12.2]AllMusic_Server-${project.version}.jar")
        relocate("net.kyori", "com.coloryr.allmusic.libs.net.kyori")
    }

    val reobfShadowJar = named<ReobfuscatedJar>("reobfShadowJar") {
        archiveFileName.set("[forge-1.12.2]AllMusic_Server-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))
    }

    configurations.shadow.get().setExtendsFrom(listOf(configurations.named("shadowImplementation").get()))

    build {
        dependsOn(reobfShadowJar)
    }
}
