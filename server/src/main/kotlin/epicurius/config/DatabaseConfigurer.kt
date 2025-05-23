package epicurius.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.storage.StorageOptions
import epicurius.Environment
import epicurius.repository.jdbi.config.configureWithAppRequirements
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class DatabaseConfigurer {

    @Value("\${postgres.database.url}")
    val postgresDbUrl: String = ""

    @Value("\${google.cloud.credentials.location}")
    val googleCredentialsFile = ""

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
        val credentials = FileInputStream(googleCredentialsFile)

        val options = StorageOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(credentials))
            .build()

        return CloudStorage(options.service, Environment.getCloudStorageBucketName())
    }

    @Bean
    fun firestore(): Firestore {
        val credentials = FileInputStream(googleCredentialsFile)

        return FirestoreOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(credentials))
            .setDatabaseId(Environment.getFirestoreDatabaseId())
            .build()
            .service
    }
}
