import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.8.21"
  kotlin("plugin.serialization") version "1.8.21"
  application
}

group = "com.github.ksugirl"
version = "0.0.1-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://jitpack.io")
}

dependencies {
  runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-jsr223:1.8.21")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.github.sisyphsu:dateparser:1.0.11")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.0")
  implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0")
  implementation("com.sksamuel.hoplite:hoplite-core:2.7.3")
  testImplementation("com.google.truth:truth:1.1.3")
  testImplementation("io.mockk:mockk:1.13.4")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
}

allOpen {
  annotation("com.github.kotlintelegrambot.Open")
}

application {
  mainClass.set("com.github.sheriff.kotlin.sun.red.ApplicationKt")
  applicationDefaultJvmArgs += "--enable-preview"
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
    jvmTarget = "19"
  }
}

tasks.withType<JavaCompile> {
  sourceCompatibility = "19"
  targetCompatibility = "19"
}

tasks.withType<Test> {
  useJUnitPlatform()
  jvmArgs("--enable-preview")
}

tasks.build {
  dependsOn(tasks.installDist)
}
