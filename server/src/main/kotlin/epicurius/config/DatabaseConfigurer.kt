package epicurius.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.StorageOptions
import epicurius.repository.jdbi.config.configureWithAppRequirements
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream

@Configuration
class DatabaseConfigurer {

    private val cloudStorageBucketName = "epicurius-bucket"

    private val postgresDbUrl: String = System.getenv("DATABASE_URL")
    private val googleCredentials: String = System.getenv("GOOGLE_CREDENTIALS")

    @Bean
    fun jdbi(): Jdbi {
        return Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(postgresDbUrl)
            }
        ).configureWithAppRequirements()
    }

    @Bean
    fun googleCloudStorage(): CloudStorage {
        val googleCredentialsStream = ByteArrayInputStream(googleCredentials.toByteArray())

        val options = StorageOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(googleCredentialsStream))
            .build()

        return CloudStorage(options.service, cloudStorageBucketName)
    }
}
