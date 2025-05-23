package epicurius.integration

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import epicurius.EpicuriusTest
import epicurius.repository.firestore.manager.FirestoreManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import java.io.FileInputStream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EpicuriusIntegrationTest : EpicuriusTest() {

    @LocalServerPort
    var port: Int = 0

    var client: WebTestClient = WebTestClient.bindToServer()
        .baseUrl("http://localhost:$port/api")
        .build()

    fun api(path: String): String = "http://localhost:$port/api$path"

    companion object {
        private const val FIRESTORE_DATABASE_ID = "epicurius-database"
        private val firestore = getFirestoreService()
        val fs = FirestoreManager(firestore)

        private fun getFirestoreService(): Firestore {
            val serviceAccount = FileInputStream(GOOGLE_CLOUD_CREDENTIALS_LOCATION)

            val options = FirestoreOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseId(FIRESTORE_DATABASE_ID)
                .build()

            return options.service
        }
    }
}
