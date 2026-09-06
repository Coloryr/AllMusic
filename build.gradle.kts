import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.1" apply false
}

// 统一项目配置
allprojects {
    // 应用插件到子项目
    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")

    group = "com.coloryr"
    version = "4.2.1"

    // 设置项目JDK版本
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_1_8

    val shadowImplementation = configurations.maybeCreate("shadowImplementation")
    configurations.named("shadow").get().extendsFrom(shadowImplementation)
    configurations.named("implementation").get().extendsFrom(shadowImplementation)

    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        maven("https://jitpack.io/")
        maven("https://maven.izzel.io/releases/")
        maven("https://maven.maxhenkel.de/repository/public")
    }

    dependencies {
        compileOnly("io.netty:netty-all:4.1.109.Final")
    }

    tasks.withType<ShadowJar> {
        mutableListOf(
            "org.apache.hc.core5",
            "org.apache.hc.client5",
            "org.slf4j",
        ).forEach { relocate(it, "com.coloryr.allmusic.libs.$it") }
    }

    tasks.named("jar") {
        dependsOn("shadowJar")
    }

    afterEvaluate {
        if (plugins.hasPlugin("com.gtnewhorizons.retrofuturagradle")) return@afterEvaluate
        if (plugins.hasPlugin("fabric-loom")) return@afterEvaluate

        val rfgObfAttr = Attribute.of("com.gtnewhorizons.retrofuturagradle.obfuscation", String::class.java)
        val rfgDeobfAttr = Attribute.of("rfgDeobfuscatorTransformed", Boolean::class.javaObjectType)
        configurations.all {
            if (!isCanBeConsumed) return@all

            attributes {
                attribute(rfgObfAttr, "mcp")
                attribute(rfgDeobfAttr, true)
            }
        }
    }
}

tasks.register("buildAll") {
    group = "build"
    dependsOn(rootProject.subprojects.filter { it.path.startsWith(":client:") }
        .map { it.tasks.named("build") })
    dependsOn(rootProject.subprojects.filter { it.path.startsWith(":server:") }
        .map { it.tasks.named("build") })
    dependsOn(rootProject.subprojects.filter { it.path.startsWith(":onejar:") }
        .map { it.tasks.named("build") })
}