plugins {
    kotlin("jvm")
    id("fabric-loom")
    `maven-publish`
    java

    // kotlinx.serialization
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
}

group = property("maven_group")!!


val mcVersion = property("minecraft_version")!!
val mcPlatform = property("minecraft_platform")!!
val buildNumber = property("build_number")!!

val versionFile = file("src/main/kotlin/VERSION.kt")
version = versionFile.readText().split("\"")[1]


repositories {
    maven(url = "https://repo.essential.gg/repository/maven-public")
    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")

    maven(url = "https://oss.jfrog.org/simple/libs-snapshot")
}

val shade by configurations.creating
configurations.modImplementation.get().extendsFrom(shade)

configurations.modImplementation {
    exclude("gg.essential", "universalcraft-1.18.1")
    exclude("gg.essential", "universalcraft-*")
}

dependencies {
    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")


    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    modImplementation(include("gg.essential:elementa-1.18.1-fabric:576+pull-104")!!)
    modImplementation(include("gg.essential:vigilance-1.18.1-fabric:280")!!)
    modImplementation(include("gg.essential:universalcraft-1.19.3-fabric:+")!!)
}

tasks {

    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        from("LICENSE")
        //  destinationDirectory.set(file("C:\\Users\\Maik\\AppData\\Roaming\\PrismLauncher\\instances\\Avalon 4\\.minecraft\\mods"))
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }

        repositories {
        }
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    // replace every occurance of @VERSION@ in any file (except this one) with the version


}

java {
    withSourcesJar()
}

