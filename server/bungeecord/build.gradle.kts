repositories {
    maven("https://libraries.minecraft.net/")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.21-R0.4")

    shadowImplementation("net.kyori:adventure-platform-bungeecord:4.4.1") {
        exclude(group = "net.md-5", module = "bungeecord-api")
    }
    shadowImplementation("net.kyori:adventure-text-minimessage:${Versions.minimessage}")
    shadowImplementation("net.kyori:adventure-api:${Versions.minimessage}")
    shadowImplementation("net.kyori:adventure-text-serializer-gson:${Versions.minimessage}")
    shadowImplementation("net.kyori:adventure-text-serializer-legacy:${Versions.minimessage}")
    shadowImplementation("net.kyori:adventure-text-serializer-plain:${Versions.minimessage}")
    shadowImplementation("net.kyori:adventure-key:${Versions.minimessage}")
}

tasks {
    processResources {
        filesMatching("bungee.yml") {
            expand(
                "version" to project.version
            )
        }
    }

    shadowJar {
        archiveFileName.set("[bungeecord]AllMusic_Server-${project.version}.jar")
        destinationDirectory.set(file("${parent!!.projectDir}/../build"))

        relocate("net.kyori", "com.coloryr.allmusic.libs.net.kyori")
    }
}
