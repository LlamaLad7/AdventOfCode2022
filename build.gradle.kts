import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}

group = "com.llamalad7"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.aballano:mnemonik:2.1.0") {
        exclude(module = "kotlinx.benchmark.runtime-jvm")
    }
    implementation("com.github.jkcclemens:khttp:0.1.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}