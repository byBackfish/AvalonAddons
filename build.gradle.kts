plugins {
    kotlin("jvm")
    id("fabric-loom")
    `maven-publish`
    java
}

group = property("maven_group")!!
version = property("mod_version")!!

loom {
    runConfigs {
        named("client") {
           // set tweak class
            property("tweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
        }
    }
}

repositories {
    maven("https://repo.polyfrost.cc/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    implementation("cc.polyfrost:oneconfig-1.16.5-fabric:0.2.0-alpha177")

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    modImplementation("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta9")
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        manifest.attributes["ModSide"] = "CLIENT"
        manifest.attributes["TweakOrder"] = 0
        manifest.attributes["ForceLoadAsMod"] = true
        manifest.attributes["TweakClass"] = "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker"
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }


}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}


