package epicurius

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import epicurius.domain.CountriesDomain
import epicurius.domain.token.Sha256TokenEncoder
import epicurius.domain.user.UserDomain
import epicurius.repository.cloudStorage.CloudStorageManager
import epicurius.repository.jdbi.utils.configureWithAppRequirements
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.io.FileInputStream

open class EpicuriusTest {

    companion object {
        private const val POSTGRES_DATABASE_URL = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"
        private const val GOOGLE_CLOUD_CREDENTIALS_LOCATION = "src/main/resources/epicurius-credentials.json"

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(POSTGRES_DATABASE_URL)
            }
        ).configureWithAppRequirements()

        // private val firestore = getFirestoreService()

        private val cloudStorage = getCloudStorageService()

        val tm = JdbiTransactionManager(jdbi)
        // val fs = FirestoreManager(firestore)
        val cs = CloudStorageManager(cloudStorage)

        private val tokenEncoder = Sha256TokenEncoder()
        private val passwordEncoder = BCryptPasswordEncoder()
        val usersDomain = UserDomain(passwordEncoder, tokenEncoder)
        val countriesDomain = CountriesDomain()

/*        private fun getFirestoreService(): Firestore {
            val serviceAccount = Environment.getGoogleServiceAccount()

            val options = FirestoreOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseId(Environment.getFirestoreDatabaseTestId())
                .build()

            return options.service
        }*/

        private fun getCloudStorageService(): Storage {
            val serviceAccount = FileInputStream(GOOGLE_CLOUD_CREDENTIALS_LOCATION)

            val options = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            return options.service
        }
    }
}
