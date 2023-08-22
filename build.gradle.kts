plugins {
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    id("com.gtnewhorizons.retrofuturagradle") version "1.3.20"
}

group = "com.slava_110.customizedwither"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

minecraft {
    mcVersion.set("1.12.2")

    mcpMappingChannel.set("stable")
    mcpMappingVersion.set("39")

    username.set("Developer")

    extraRunJvmArguments.addAll(
        "-ea:${project.group}",
        "-Dfml.coreMods.load=com.slava_110.customizedwither.CustomizedWitherMixinLoader"
    )

    groupsToExcludeFromAutoReobfMapping.addAll("com.diffplug", "com.diffplug.durian", "net.industrial-craft")
}

repositories {
    maven {
        name = "CleanroomMC Maven"
        url = uri("https://maven.cleanroommc.com")
    }
    maven {
        name = "SpongePowered Maven"
        url = uri("https://repo.spongepowered.org/maven")
    }
    maven {
        name = "CurseMaven"
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

dependencies {

    val mixin: String = modUtils.enableMixins(
        "zone.rong:mixinbooter:8.3",
        "mixins.customizedwither.refmap.json"
    ) as String

    api(mixin) {
        isTransitive = false
    }
    annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
    annotationProcessor("com.google.guava:guava:24.1.1-jre")
    annotationProcessor("com.google.code.gson:gson:2.8.6")
    annotationProcessor(mixin) {
        isTransitive = false
    }

    runtimeOnly(mixin)
}

tasks.named<JavaExec>("runObfClient") {
    workingDir("run-client-obf")
}

tasks.named<JavaExec>("runObfServer") {
    mainClass.set("net.minecraftforge.fml.relauncher.ServerLaunchWrapper")
    workingDir("run-server-obf")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("mcmod.info") {
        expand(mapOf("modVersion" to project.version))
    }
}

tasks.jar {
    manifest {
        attributes(
            "FMLCorePlugin" to "com.slava_110.customizedwither.CustomizedWitherMixinLoader",
            "FMLCorePluginContainsFMLMod" to true
        )
    }
}

// IDE Settings
idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
        inheritOutputDirs = true // Fix resources in IJ-Native runs
    }
}

tasks.processIdeaSettings {
    dependsOn(tasks.injectTags)
}