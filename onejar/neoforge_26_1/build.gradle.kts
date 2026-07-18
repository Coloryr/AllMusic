plugins {
    id("dev.architectury.loom-no-remap") version Versions.architecturyLoom
//    id("architectury-plugin") version "3.5-SNAPSHOT"
}

java.sourceCompatibility = JavaVersion.VERSION_25
java.targetCompatibility = JavaVersion.VERSION_25

// 直接引用 client 和 server 模块的源码目录，无需手动复制
val clientSourceDir = project(":client:neoforge_26_1").projectDir.resolve("src/main/java/com/coloryr/allmusic/client")
val serverSourceDir = project(":server:neoforge_26_1").projectDir.resolve("src/main/java/com/coloryr/allmusic/server")
val commSourceDir = project(":server:neoforge_26_1").projectDir.resolve("src/main/java/com/coloryr/allmusic/comm")
val clientResDir = project(":client:neoforge_26_1").projectDir.resolve("src/main/resources")

sourceSets {
    main {
        java {
            setSrcDirs(listOf(clientSourceDir, serverSourceDir, commSourceDir))
        }
        resources {
            setSrcDirs(listOf(
                file("src/main/resources"),  // onejar 自己的合并资源（fabric.mod.json）
                clientResDir,
            ))
        }
    }
}

//architectury {
//  platformSetupLoomIde()
//  neoForge()
//}

repositories {
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:26.1")
    neoForge("net.neoforged:neoforge:26.1.0.19-beta")

    compileOnly("icyllis.modernui:ModernUI-NeoForge:26.1.2-3.13.0.4")
    compileOnly("de.maxhenkel.voicechat:voicechat-api:2.6.0")

    implementation(include("net.kyori:adventure-platform-neoforge:6.9.0")!!)
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        filesMatching("META-INF/neoforge.mods.toml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        archiveFileName.set("[neoforge-26.1]AllMusic-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))
    }

    build {
        dependsOn(shadowJar)
    }
}
