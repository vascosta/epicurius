package epicurius.repository

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import epicurius.Environment
import epicurius.domain.UserDomain
import epicurius.domain.token.Sha256TokenEncoder
import epicurius.repository.jdbi.utils.configure
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

open class EpicuriusTest {

    companion object {
        val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(Environment.getPostgresDbUrl())
            }
        ).configure()

        val firestore = getFirestoreService()

        private val tokenEncoder = Sha256TokenEncoder()
        private val passwordEncoder = BCryptPasswordEncoder()
        val usersDomain = UserDomain(passwordEncoder, tokenEncoder)

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