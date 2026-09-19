plugins {
    id("net.fabricmc.fabric-loom") version Versions.fabricLoom
}

java.sourceCompatibility = JavaVersion.VERSION_25
java.targetCompatibility = JavaVersion.VERSION_25

dependencies {
    minecraft("com.mojang:minecraft:26.3")
    implementation("net.fabricmc:fabric-loader:0.19.5")

    implementation("net.fabricmc.fabric-api:fabric-api:0.161.0+26.3")

    implementation(include("net.kyori:adventure-platform-fabric:7.2.0")!!)
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version
            )
        }
    }

    // loom 的 jar-in-jar 嵌套默认只挂在 jar 任务上，需要手动接到 shadowJar
    loom.nestJars(named("shadowJar", Jar::class), configurations.getByName("include"))

    shadowJar {
        archiveFileName.set("[fabric-26.3]AllMusic_Server-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))
    }

    build {
        dependsOn(shadowJar)
    }
}
