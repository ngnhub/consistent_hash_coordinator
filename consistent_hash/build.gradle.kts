plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
}

val artifactName = "consistent_hash"
val artifactVersion = "1.0-SNAPSHOT"

group = "com.github.ngnhub"
version = artifactVersion

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "$group"
            artifactId = artifactName
            version = artifactVersion
        }
    }
    repositories {
        mavenLocal()
    }
}

// versions
val guavaVersion = "33.3.1-jre"
val mockkVersion = "1.13.13"
val paramTestVersion = "5.11.3"
val slf4jVersion = "2.0.16"
val loggerVersion = "7.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:$guavaVersion")

    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$paramTestVersion")
    testImplementation(kotlin("test"))
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "failed")
    }
}

kotlin {
    jvmToolchain(21)
}