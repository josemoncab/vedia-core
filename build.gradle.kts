plugins {
    id("java-library")
    id("maven-publish")
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
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
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

    register<Copy>("copyToFolder") {
        group = "build"

        from("build/libs")
        include("vedia-core*.jar")
        into("D:\\JavaLibs")

        dependsOn(build.get())
    }
}