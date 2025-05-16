package epicurius

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import epicurius.domain.fridge.FridgeDomain
import epicurius.domain.picture.PictureDomain
import epicurius.domain.token.Sha256TokenEncoder
import epicurius.domain.user.CountriesDomain
import epicurius.domain.user.UserDomain
import epicurius.repository.firestore.manager.FirestoreManager
import epicurius.repository.jdbi.config.configureWithAppRequirements
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.io.FileInputStream

open class EpicuriusTest {

    companion object {
        private const val POSTGRES_TEST_DATABASE_URL = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"
        private const val FIRESTORE_TEST_DATABASE_ID = "epicurius-test-database"
        const val GOOGLE_CLOUD_CREDENTIALS_LOCATION = "src/main/resources/epicurius-credentials.json"

        val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(POSTGRES_TEST_DATABASE_URL)
            }
        ).configureWithAppRequirements()
        private val firestore = getFirestoreService()
        val tm = JdbiTransactionManager(jdbi)
        val fs = FirestoreManager(firestore)

        init {
            jdbi.useHandle<Exception> {
                it.execute("DELETE FROM dbo.meal_planner_recipe")
                it.execute("DELETE FROM dbo.meal_planner")
                it.execute("DELETE FROM dbo.collection_recipe")
                it.execute("DELETE FROM dbo.collection")
                it.execute("DELETE FROM dbo.ingredient")
                it.execute("DELETE FROM dbo.recipe_rating")
                it.execute("DELETE FROM dbo.recipe")
                it.execute("DELETE FROM dbo.fridge")
                it.execute("DELETE FROM dbo.followers")
                it.execute("DELETE FROM dbo.token")
                it.execute("DELETE FROM dbo.user")
            }
        }

        private fun getFirestoreService(): Firestore {
            val serviceAccount = FileInputStream(GOOGLE_CLOUD_CREDENTIALS_LOCATION)

            val options = FirestoreOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseId(FIRESTORE_TEST_DATABASE_ID)
                .build()

            return options.service
        }

        val userDomain = UserDomain(BCryptPasswordEncoder(), Sha256TokenEncoder())
        val pictureDomain = PictureDomain()

        val testPicture =
            MockMultipartFile(
                "test-picture.jpeg",
                "test-picture.jpeg",
                "image/jpeg",
                FileInputStream("src/test/resources/test-picture.jpeg")
            )

        val testPicture2 = MockMultipartFile(
            "test-picture2.jpeg",
            "test-picture2.jpeg",
            "image/jpg",
            FileInputStream("src/test/resources/test-picture2.jpg")
        )

        val testTomatoPicture =
            MockMultipartFile(
                "test-tomato.jpeg",
                "test-tomato.jpeg",
                "image/jpeg",
                FileInputStream("src/test/resources/test-tomato.jpeg")
            )
    }
}
