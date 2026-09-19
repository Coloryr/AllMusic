plugins {
    id("net.fabricmc.fabric-loom") version Versions.fabricLoom
}

java.sourceCompatibility = JavaVersion.VERSION_25
java.targetCompatibility = JavaVersion.VERSION_25

dependencies {
    minecraft("com.mojang:minecraft:26.3")
    implementation("net.fabricmc:fabric-loader:0.19.5")

    implementation("net.fabricmc.fabric-api:fabric-api:0.161.0+26.3")

    compileOnly("de.maxhenkel.voicechat:voicechat-api:2.6.0")
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
        archiveFileName.set("[fabric-26.3]AllMusic_Client-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))
    }

    build {
        dependsOn(shadowJar)
    }
}
