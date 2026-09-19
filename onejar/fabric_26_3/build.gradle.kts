plugins {
    id("net.fabricmc.fabric-loom") version Versions.fabricLoom
}

java.sourceCompatibility = JavaVersion.VERSION_25
java.targetCompatibility = JavaVersion.VERSION_25

// 直接引用 client 和 server 模块的源码目录，无需手动复制
val clientSourceDir = project(":client:fabric_26_3").projectDir.resolve("src/main/java/com/coloryr/allmusic/client")
val serverSourceDir = project(":server:fabric_26_3").projectDir.resolve("src/main/java/com/coloryr/allmusic/server")
val commSourceDir = project(":server:fabric_26_3").projectDir.resolve("src/main/java/com/coloryr/allmusic/comm")
val clientResDir = project(":client:fabric_26_3").projectDir.resolve("src/main/resources")
val serverResDir = project(":server:fabric_26_3").projectDir.resolve("src/main/resources")

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
    minecraft("com.mojang:minecraft:26.3")
    implementation("net.fabricmc:fabric-loader:0.19.5")

    implementation("net.fabricmc.fabric-api:fabric-api:0.161.0+26.3")

    compileOnly("de.maxhenkel.voicechat:voicechat-api:2.6.0")

    implementation(include("net.kyori:adventure-platform-fabric:7.2.0")!!)
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

    // loom 的 jar-in-jar 嵌套默认只挂在 jar 任务上，需要手动接到 shadowJar
    loom.nestJars(named("shadowJar", Jar::class), configurations.getByName("include"))

    shadowJar {
        archiveFileName.set("[fabric-26.3]AllMusic-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))

//        relocate("net.kyori", "com.coloryr.allmusic.libs.net.kyori")
//        relocate("com.google.gson", "com.coloryr.allmusic.libs.com.google.gson")
    }

    build {
        dependsOn(shadowJar)
    }
}
