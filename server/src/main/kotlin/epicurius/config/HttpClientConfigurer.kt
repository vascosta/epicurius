package epicurius.config

import kotlinx.coroutines.future.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Configuration
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Configuration
class HttpClientConfigurer : HttpClientConfig {
    val httpClient: HttpClient = HttpClient.newHttpClient()

    override suspend fun get(uri: String): String =
        httpClient.sendAsync(
            HttpRequest.newBuilder(URI(uri))
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).await().body()

    override suspend fun post(uri: String, body: Map<String, String>): String =
        httpClient.sendAsync(
            HttpRequest.newBuilder(URI(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Json.encodeToString(body)))
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).await().body()
}
