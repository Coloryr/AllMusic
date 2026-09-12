import net.fabricmc.loom.build.nesting.NestableJarGenerationTask

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
    minecraft("com.mojang:minecraft:26.1")
    neoForge("net.neoforged:neoforge:26.1.0.19-beta")

    implementation(include("net.kyori:adventure-platform-neoforge:6.9.0")!!)
}

tasks {
    processResources {
        filesMatching("META-INF/neoforge.mods.toml") {
            expand("version" to project.version)
        }
    }

    // loom 的 jar-in-jar 嵌套默认只挂在 jar 任务上，需要手动接到 shadowJar
    val processIncludeJars = named("processIncludeJars", NestableJarGenerationTask::class)
    val shadowJarTask = named("shadowJar", Jar::class)
    shadowJarTask.configure { dependsOn(processIncludeJars) }
    loom.nestJars(
        shadowJarTask,
        files(fileTree(processIncludeJars.flatMap { it.outputDirectory }).matching { include("**/*.jar") })
    )

    shadowJar {
        archiveFileName.set("[neoforge-26.1]AllMusic_Server-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))
    }

    build {
        dependsOn(shadowJar)
    }
}
