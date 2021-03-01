package io.nais

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
class IOTest {
   private val tmpDir = System.getProperty("java.io.tmpdir")

   @Test
   fun `platform is recognized as JVM_MAVEN if pom xml is present`() {
      val pomXml = File("$tmpDir/pom.xml").apply { createNewFile() }
      assertEquals("JVM_MAVEN", determinePlatform(tmpDir))
      pomXml.delete()
   }

   @Test
   fun `platform is recognized as JVM_GRADLE if build gradle is present`() {
      val buildGradle = File("$tmpDir/build.gradle").apply { createNewFile() }
      assertEquals("JVM_GRADLE", determinePlatform(tmpDir))
      buildGradle.delete()
   }

   @Test
   fun `platform is recognized as JVM_GRADLE if build gradle kts is present`() {
      val buildGradleKts = File("$tmpDir/build.gradle.kts").apply { createNewFile() }
      assertEquals("JVM_GRADLE", determinePlatform(tmpDir))
      buildGradleKts.delete()
   }

   @Test
   fun `platform is recognized as NODEJS if build package json is present`() {
      val packageJson = File("$tmpDir/package.json").apply { createNewFile() }
      assertEquals("NODEJS", determinePlatform(tmpDir))
      packageJson.delete()
   }

   @Test
   fun `filepath with lfi is rejected`() {
      val pathWithLFI = "./../../whatever"
      assertThrows<IllegalArgumentException> { writeAppConfig(mapOf(pathWithLFI to "whatever content"), tmpDir) }
   }

   @Test
   fun `filepath on the same level as project dir is ok`() {
      val goodPath = "$tmpDir/./whatever"
      assertDoesNotThrow { writeAppConfig(mapOf(goodPath to "whatever content"), tmpDir) }
   }

   @Test
   fun `filepath below project dir is ok`() {
      val goodPath = "$tmpDir/something/further/below"
      Files.createDirectories(Paths.get(goodPath).normalize())
      assertDoesNotThrow { writeAppConfig(mapOf(goodPath to "whatever content"), tmpDir) }
   }

}
