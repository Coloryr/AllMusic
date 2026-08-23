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

tasks {
    processResources {
        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        archiveFileName.set("[forge-1.12.2]AllMusic_Client-${project.version}.jar")
    }

    val reobfShadowJar = named<ReobfuscatedJar>("reobfShadowJar") {
        archiveFileName.set("[forge-1.12.2]AllMusic_Client-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))
    }

    configurations.shadow.get().setExtendsFrom(listOf(configurations.named("shadowImplementation").get()))

    build {
        dependsOn(reobfShadowJar)
    }
}
