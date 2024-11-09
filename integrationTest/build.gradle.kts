plugins {
    kotlin("jvm")
}

group = "me.centralhardware.telegram"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":"))
    implementation("dev.inmo:tgbotapi:20.0.0")
}

tasks.test {
    useJUnitPlatform()
}