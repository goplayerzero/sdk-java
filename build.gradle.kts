import java.util.*
import net.researchgate.release.GitAdapter
import net.researchgate.release.ReleaseExtension

group = "app.playerzero.sdk"

plugins {
    `java-library`
    `maven-publish`
    signing
    id("net.researchgate.release") version "2.8.1"
    id("me.qoomon.git-versioning") version "6.4.1"
}

defaultTasks("clean", "build")

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:${properties["slf4j_version"]}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${properties["jackson_version"]}")
    runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:${properties["jackson_version"]}")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-parameter-names:${properties["jackson_version"]}")

    implementation("ch.qos.logback:logback-core:${properties["logback_version"]}") {
        isTransitive = false
    }
    implementation("ch.qos.logback:logback-classic:${properties["logback_version"]}") {
        isTransitive = false
    }
    implementation("org.apache.logging.log4j:log4j-api:${properties["log4j_version"]}") {
        isTransitive = false
    }
    implementation("org.apache.logging.log4j:log4j-core:${properties["log4j_version"]}") {
        isTransitive = false
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
}

gitVersioning.apply {
    refs {
        branch("main") {
            version =
                if (project.version.toString().endsWith("-SNAPSHOT")) "main-SNAPSHOT"
                else "\${version}"
        }
        branch(".+") {
            version = "\${ref}-SNAPSHOT"
        }
        tag("(?<version>.*)") {
            version = "\${ref.version}"
        }
    }

    // optional fallback configuration in case of no matching ref configuration
    rev {
        version = "\${commit}"
    }
}

fun ReleaseExtension.git(configureFn: GitAdapter.GitConfig.() -> Unit) {
    (propertyMissing("git") as GitAdapter.GitConfig).configureFn()
}

release {
    failOnCommitNeeded = true
    failOnPublishNeeded = false
    failOnSnapshotDependencies = true
    failOnUpdateNeeded = true
    failOnUnversionedFiles = true
    revertOnFail = true
    git {
        requireBranch = "main"
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "java-api"
            from(components["java"])

            pom {
                name.set(artifactId)
                packaging = "jar"
                description.set("PlayerZero SDK for Java")
                url.set("https://github.com/goplayerzero/sdk-java")

                scm {
                    connection.set("scm:git:git://github.com/goplayerzero/sdk-java.git")
                    developerConnection.set("scm:git:git://github.com/goplayerzero/sdk-java")
                    url.set("https://github.com/goplayerzero/sdk-java")
                }

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://raw.githubusercontent.com/goplayerzero/sdk-java/main/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("sepatel")
                        name.set("Sejal Patel")
                        email.set("sejal@playerzero.app")
                    }
                }
            }
        }
    }
    repositories {
        if (version.toString().endsWith("-SNAPSHOT")) {
            maven("https://nexus.playerzero.app/repository/maven-snapshots/") {
                credentials {
                    username = System.getenv("NEXUS_USERNAME") ?: project.findProperty("pzNexusUsername").toString()
                    password = System.getenv("NEXUS_PASSWORD") ?: project.findProperty("pzNexusPassword").toString()
                }
            }
        } else {
            maven("https://nexus.playerzero.app/repository/maven-releases/") {
                credentials {
                    username = System.getenv("NEXUS_USERNAME") ?: project.findProperty("pzNexusUsername").toString()
                    password = System.getenv("NEXUS_PASSWORD") ?: project.findProperty("pzNexusPassword").toString()
                }
            }
            maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
                credentials {
                    username = System.getenv("SONATYPE_USERNAME") ?: project.findProperty("ossPzUsername").toString()
                    password = System.getenv("SONATYPE_PASSWORD") ?: project.findProperty("ossPzPassword").toString()
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    isRequired = true
    sign(publishing.publications)
}

tasks {
    withType<Wrapper> {
        gradleVersion = "7.5.1"
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<Jar> {
        manifest {
            attributes(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Build-Date" to Date()
            )
        }
    }

    withType<Sign>().configureEach {
        onlyIf { !version.toString().endsWith("-SNAPSHOT") }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}
