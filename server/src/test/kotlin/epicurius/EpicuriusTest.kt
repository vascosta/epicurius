package epicurius

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import epicurius.domain.CountriesDomain
import epicurius.domain.token.Sha256TokenEncoder
import epicurius.domain.user.UserDomain
import epicurius.repository.jdbi.utils.configureWithAppRequirements
import epicurius.repository.transaction.firestore.FirestoreManager
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

open class EpicuriusTest {

    companion object {
        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(Environment.getPostgresDbUrl())
            }
        ).configureWithAppRequirements()

        private val firestore = getFirestoreService()

        val tm = JdbiTransactionManager(jdbi)
        val fs = FirestoreManager(firestore)

        private val tokenEncoder = Sha256TokenEncoder()
        private val passwordEncoder = BCryptPasswordEncoder()
        val usersDomain = UserDomain(passwordEncoder, tokenEncoder)
        val countriesDomain = CountriesDomain()

        private fun getFirestoreService(): Firestore {
            val serviceAccount = Environment.getFirestoreServiceAccount()

            val options = FirestoreOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseId(Environment.getFirestoreDatabaseTestId())
                .build()

            return options.service
        }
    }
}