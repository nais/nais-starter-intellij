package io.nais

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Base64
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name
import kotlin.streams.toList

fun requestAppConfig(formData: FormData): Map<String, String> = HttpClients.createDefault().use { httpClient ->
   val jsonMapper = jacksonObjectMapper()
   val post = HttpPost("https://start.external.prod-gcp.nav.cloud.nais.io/app").apply {
      addHeader("Content-Type", "application/json")
      addHeader("Accept", "application/json")
      entity = StringEntity(jsonMapper.writeValueAsString(formData))
   }
   httpClient.execute(post).use { response ->
      if (response.isError()) throw RuntimeException(response.errorMessage())
      toKeyValue(EntityUtils.toString(response.entity))
   }
}

@ExperimentalPathApi
fun determinePlatform(dirPath: String) = Paths.get(dirPath).let { path ->
   val filesInProjectDir = Files.list(path).map { it.name }.toList()
   platforms.filter { filesInProjectDir.contains(it.key) }.map { it.value }.firstOrNull()
}

@ExperimentalPathApi
fun writeAppConfig(data: Map<String, String>, projectDir: String) {
   data.forEach { (key, value) ->
      val projectPath = Paths.get(projectDir).normalize()
      val filePath = Paths.get(projectDir, key).normalize()
      val dirPath = filePath.parent
      if (dirPath.isParentOf(projectPath)) throw IllegalArgumentException("Filename $key looks kinda suspicious, possibly LFI?")
      Files.createDirectories(dirPath)
      Files.writeString(filePath, String(Base64.getDecoder().decode(value)))
   }
}

private val platforms = mapOf(
   "pom.xml" to "JVM_MAVEN",
   "build.gradle" to "JVM_GRADLE",
   "build.gradle.kts" to "JVM_GRADLE",
   "package.json" to "NODEJS",
   "go.mod" to "GO_MAKE",
   "poetry.lock" to "PYTHON_POETRY",
   "requirements.txt" to "PYTHON_PIP",
)

private fun toKeyValue(json: String) = jacksonObjectMapper().readTree(json).let { jsonNode ->
   jacksonObjectMapper().convertValue(jsonNode, object : TypeReference<Map<String, String>>() {})
}

private fun Path.isParentOf(other: Path) = this.nameCount < other.nameCount

private fun CloseableHttpResponse.isError() = statusLine.statusCode != 200
private fun CloseableHttpResponse.errorMessage() = "Got ${statusLine.statusCode} ${statusLine.reasonPhrase} from server"





