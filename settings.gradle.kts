rootProject.name = "AllMusic"

include(":codec")

include(":client")

include(":client:fabric_1_16_5")
include(":client:fabric_1_20_1")
include(":client:fabric_1_21")
include(":client:fabric_1_21_3")
include(":client:fabric_1_21_4")
include(":client:fabric_1_21_6")
include(":client:fabric_1_21_8")
include(":client:fabric_1_21_11")
include(":client:fabric_26_1")
include(":client:fabric_26_2")

include(":client:forge_1_7_10")
include(":client:forge_1_12_2")
include(":client:forge_1_16_5")
include(":client:forge_1_20_1")

include(":client:neoforge_1_21")
include(":client:neoforge_1_21_3")
include(":client:neoforge_1_21_4")
include(":client:neoforge_1_21_6")
include(":client:neoforge_1_21_11")
include(":client:neoforge_26_1")
include(":client:neoforge_26_2")

include(":server")

include(":server:fabric_1_16_5")
include(":server:fabric_1_20_1")
include(":server:fabric_1_21")
include(":server:fabric_1_21_6")
include(":server:fabric_1_21_11")
include(":server:fabric_26_1")
include(":server:fabric_26_2")

include(":server:forge_1_7_10")
include(":server:forge_1_12_2")
include(":server:forge_1_16_5")
include(":server:forge_1_20_1")

include(":server:neoforge_1_21")
include(":server:neoforge_1_21_6")
include(":server:neoforge_1_21_11")
include(":server:neoforge_26_1")
include(":server:neoforge_26_2")

include(":server:spigot")
include(":server:paper")
include(":server:folia")
include(":server:paper")
include(":server:velocity")

include(":onejar")

include(":onejar:fabric_1_16_5")
include(":onejar:fabric_1_20_1")
include(":onejar:fabric_1_21")
include(":onejar:fabric_1_21_6")
include(":onejar:fabric_1_21_11")
include(":onejar:fabric_26_1")
include(":onejar:fabric_26_2")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.architectury.dev/")
        maven("https://nexus.gtnewhorizons.com/repository/public/")
    }
}
