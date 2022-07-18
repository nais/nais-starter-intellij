import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val junitJupiterVersion = "5.8.0-M1"

plugins {
   id("java")
   id("org.jetbrains.kotlin.jvm") version "1.5.20"
   id("org.jetbrains.intellij") version "0.6.3"
}

group = "io.nais"

tasks.withType<JavaCompile> {
   sourceCompatibility = "11"
   targetCompatibility = "11"
}
listOf("compileKotlin", "compileTestKotlin").forEach {
   tasks.getByName<KotlinCompile>(it) {
      kotlinOptions.jvmTarget = "11"
   }
}

repositories {
   mavenCentral()
}

dependencies {
   implementation(kotlin("stdlib-jdk8"))

   testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
   testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}
java {
   sourceCompatibility = JavaVersion.VERSION_11
   targetCompatibility = JavaVersion.VERSION_11
}

intellij {
   version = "2020.3"
   pluginName = "NAIS starter"
   updateSinceUntilBuild = false
}

tasks {
   withType<Test> {
      useJUnitPlatform()
      testLogging {
         events("passed", "skipped", "failed")
         exceptionFormat = FULL
      }
   }

   withType<org.jetbrains.intellij.tasks.PublishTask> {
      setToken(System.getenv("JETBRAINS_MARKETPLACE_TOKEN"))
   }
}

