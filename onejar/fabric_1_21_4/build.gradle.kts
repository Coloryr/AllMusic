plugins {
    id("fabric-loom") version Versions.fabricLoom
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

// 直接引用 client 和 server 模块的源码目录，无需手动复制
val clientSourceDir = project(":client:fabric_1_21").projectDir.resolve("src/main/java/com/coloryr/allmusic/client")
val serverSourceDir = project(":server:fabric_1_21").projectDir.resolve("src/main/java/com/coloryr/allmusic/server")
val commSourceDir = project(":server:fabric_1_21").projectDir.resolve("src/main/java/com/coloryr/allmusic/comm")
val clientResDir = project(":client:fabric_1_21").projectDir.resolve("src/main/resources")
val serverResDir = project(":server:fabric_1_21").projectDir.resolve("src/main/resources")

sourceSets {
    main {
        java {
            setSrcDirs(listOf(clientSourceDir, serverSourceDir, commSourceDir))
        }
        resources {
            setSrcDirs(listOf(
                file("src/main/resources"),  // onejar 自己的合并资源（fabric.mod.json）
                clientResDir,
                serverResDir
            ))
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:1.21")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.16.10")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.100.1+1.21")

    modImplementation(include("net.kyori:adventure-platform-fabric:5.14.1")!!)
}

tasks {
    processResources {
        // onejar 自己的 fabric.mod.json 优先（排在 srcDirs 第一位），
        // client/server 的 fabric.mod.json 作为重复项被排除
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version
            )
        }
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)

        archiveFileName.set("[fabric-1.21]AllMusic-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))
    }

    build {
        dependsOn(remapJar)
    }
}
