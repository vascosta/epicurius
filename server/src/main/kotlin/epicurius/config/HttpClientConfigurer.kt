package epicurius.config

import kotlinx.coroutines.future.await
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
            HttpRequest.newBuilder(URI(uri)).build(),
            HttpResponse.BodyHandlers.ofString()
        ).await().body()
}
