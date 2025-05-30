package epicurius.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.storage.StorageOptions
import epicurius.repository.jdbi.config.configureWithAppRequirements
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class DatabaseConfigurer {

    private val cloudStorageBucketName = "epicurius-bucket"
    private val firestoreDatabaseId = "epicurius-database"
    private val googleCredentialsFileName = "epicurius-credentials.json"

    private val postgresDbUrl: String = System.getenv("DATABASE_URL")

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
        val googleCredentials = this::class.java.classLoader.getResourceAsStream(googleCredentialsFileName)

        val options = StorageOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(googleCredentials))
            .build()

        return CloudStorage(options.service, cloudStorageBucketName)
    }

    @Bean
    fun firestore(): Firestore {
        val googleCredentials = this::class.java.classLoader.getResourceAsStream(googleCredentialsFileName)

        return FirestoreOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(googleCredentials))
            .setDatabaseId(firestoreDatabaseId)
            .build()
            .service
    }


}
