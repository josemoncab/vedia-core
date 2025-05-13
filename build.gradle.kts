plugins {
    id("java-library")
    id("maven-publish")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
}

group = "com.vediastudios"
version = "${project.property("core-version")}"

var javaVersion = 21

repositories {
    mavenCentral()

    // Paper Repo
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    // Libby (AlessioDP) Repository
    maven {
        name = "AlessioDP"
        url = uri("https://repo.alessiodp.com/releases/")
    }
}

dependencies {
    paperweight.paperDevBundle("${project.property("minecraft")}-R0.1-SNAPSHOT")

    implementation("net.byteflux:libby-bukkit:${project.property("libby")}")
    compileOnly("org.snakeyaml:snakeyaml-engine:2.9")
}

java {
    withSourcesJar()
    withJavadocJar()

    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
}

publishing {
    publications {
        create<MavenPublication>("jitpack") {
            groupId = "com.vediastudios"
            artifactId = "vedia-core"
            version = "${project.property("core-version")}"

            from(components["java"])

            pom {
                name = "Vedia Core"
                description = "Core library for all VediaStudios plugins"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/josemoncab/vedia-core/blob/master/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "josemoncab"
                        name = "Jose Montalvillo"
                    }
                }
            }
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(javaVersion)
    }

    compileJava.get().dependsOn(clean)


    // Dev task
    register<Copy>("copyToFolder") {
        group = "build"

        from("build/libs")
        include("vedia-core*.jar")
        into("D:\\JavaLibs")
        rename("vedia-core-[0-9]+\\.[0-9]+\\.[0-9]+\\.jar", "vedia-core.jar")

        dependsOn(build.get())
    }
}