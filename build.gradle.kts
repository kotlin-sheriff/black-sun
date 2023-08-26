import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.9.10"
  kotlin("plugin.serialization") version "1.9.10"
  application
}

group = "com.github.ksugirl"
version = "0.0.1-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://jitpack.io")
}

dependencies {
  runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-jsr223:1.9.10")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.github.sisyphsu:dateparser:1.0.11")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.0")
  implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0")
  implementation("com.sksamuel.hoplite:hoplite-core:2.7.3")
  testImplementation("com.google.truth:truth:1.1.3")
  testImplementation("io.mockk:mockk:1.13.4")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")
}

application {
  mainClass.set("com.github.sheriff.kotlin.sun.red.ApplicationKt")
  applicationDefaultJvmArgs += "--enable-preview"
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "19"
    freeCompilerArgs += listOf(
      "-Xjsr305=strict",
      "-Xvalue-classes",
      "-opt-in=kotlin.ExperimentalStdlibApi",
      "-opt-in=kotlin.time.ExperimentalTime",
      "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
    )
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
