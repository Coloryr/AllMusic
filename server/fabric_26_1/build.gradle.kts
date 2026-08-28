plugins {
    id("net.fabricmc.fabric-loom") version Versions.fabricLoom
}

java.sourceCompatibility = JavaVersion.VERSION_25
java.targetCompatibility = JavaVersion.VERSION_25

dependencies {
    minecraft("com.mojang:minecraft:26.1")
    implementation("net.fabricmc:fabric-loader:0.18.6")

    implementation("net.fabricmc.fabric-api:fabric-api:0.145.1+26.1")

    implementation(include("net.kyori:adventure-platform-fabric:6.9.0")!!)
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
        archiveFileName.set("[fabric-26.1]AllMusic_Server-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))
    }

    build {
        dependsOn(shadowJar)
    }
}
