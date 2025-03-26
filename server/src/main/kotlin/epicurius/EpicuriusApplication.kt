package epicurius

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import epicurius.domain.token.Sha256TokenEncoder
import epicurius.http.pipeline.authentication.AuthenticatedUserArgumentResolver
import epicurius.http.pipeline.authentication.AuthenticationInterceptor
import epicurius.http.utils.HttpClientConfig
import epicurius.repository.jdbi.utils.configureWithAppRequirements
import kotlinx.coroutines.future.await
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@SpringBootApplication
class EpicuriusApplication {
    @Bean
    fun jdbi(): Jdbi {
        return Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(Environment.getPostgresDbUrl())
            }
        ).configureWithAppRequirements()
    }

/*    @Bean
    fun firestore(): Firestore {
        val serviceAccount = Environment.getGoogleServiceAccount()

        val options = FirestoreOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseId(Environment.getFirestoreDatabaseId())
            .build()

        return options.service
    }*/

    @Bean
    fun googleStorage(): Storage {
        val serviceAccount = Environment.getGoogleServiceAccount()

        val options = StorageOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        return options.service
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()
}

@Configuration
class PipelineConfigurer(
    val authenticationInterceptor: AuthenticationInterceptor,
    val authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authenticatedUserArgumentResolver)
    }
}

@Configuration
class HttpClientConfigurer: HttpClientConfig {
    val httpClient: HttpClient = HttpClient.newHttpClient()

    override suspend fun get(uri: String): String =
        httpClient.sendAsync(
            HttpRequest.newBuilder(URI(uri)).build(),
            HttpResponse.BodyHandlers.ofString()
        ).await().body()
}

fun main(args: Array<String>) {
    runApplication<EpicuriusApplication>(*args)
}
