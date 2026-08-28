plugins {
    id("net.fabricmc.fabric-loom") version Versions.fabricLoom
}

java.sourceCompatibility = JavaVersion.VERSION_25
java.targetCompatibility = JavaVersion.VERSION_25

dependencies {
    minecraft("com.mojang:minecraft:26.2")
    implementation("net.fabricmc:fabric-loader:0.19.3")

    implementation("net.fabricmc.fabric-api:fabric-api:0.152.1+26.2")

    implementation(include("net.kyori:adventure-platform-fabric:7.0.0")!!)
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version
            )
        }
    }

    shadowJar {
        archiveFileName.set("[fabric-26.2]AllMusic_Server-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))
    }

    build {
        dependsOn(shadowJar)
    }
}
