plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.github.ngnhub"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

// versions
val consistentHashVersion = "1.0-SNAPSHOT"
val coroutinesVersion = "1.9.0"
val mockVersion = "5.4.0"

dependencies {
    implementation("com.github.ngnhub:consistent_hash:$consistentHashVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "failed")
    }
}
kotlin {
    jvmToolchain(21)
}
