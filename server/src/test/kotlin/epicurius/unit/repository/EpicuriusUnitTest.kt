package epicurius.unit.repository

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.storage.StorageOptions
import epicurius.config.CloudStorage
import epicurius.config.HttpClientConfigurer
import epicurius.domain.PictureDomain
import epicurius.domain.fridge.FridgeDomain
import epicurius.domain.token.Sha256TokenEncoder
import epicurius.domain.user.CountriesDomain
import epicurius.domain.user.UserDomain
import epicurius.repository.cloudStorage.CloudStorageManager
import epicurius.repository.firestore.FirestoreManager
import epicurius.repository.jdbi.config.configureWithAppRequirements
import epicurius.repository.spoonacular.SpoonacularManager
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.io.FileInputStream

open class EpicuriusUnitTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun clearDB() {
            jdbi.useHandle<Exception> {
                it.execute("DELETE FROM dbo.calories")
                it.execute("DELETE FROM dbo.meal_planner_recipe")
                it.execute("DELETE FROM dbo.meal_planner")
                it.execute("DELETE FROM dbo.collection")
                it.execute("DELETE FROM dbo.ingredient")
                it.execute("DELETE FROM dbo.recipe_rating")
                it.execute("DELETE FROM dbo.recipe")
                it.execute("DELETE FROM dbo.followers")
                it.execute("DELETE FROM dbo.fridge")
                it.execute("DELETE FROM dbo.user")
            }
        }

        private const val POSTGRES_DATABASE_URL = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"
        private const val GOOGLE_CLOUD_CREDENTIALS_LOCATION = "src/main/resources/epicurius-credentials.json"
        private const val FIRESTORE_TEST_DATABASE_ID = "epicurius-test-database"
        private const val GOOGLE_CLOUD_STORAGE_TEST_BUCKET = "epicurius-test-bucket"

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(POSTGRES_DATABASE_URL)
            }
        ).configureWithAppRequirements()

        private val firestore = getFirestoreService()
        private val cloudStorage = getCloudStorageService()
        private val httpClient = HttpClientConfigurer()

        val tm = JdbiTransactionManager(jdbi)
        val fs = FirestoreManager(firestore)
        val cs = CloudStorageManager(cloudStorage)
        val sm = SpoonacularManager(httpClient)

        val usersDomain = UserDomain(BCryptPasswordEncoder(), Sha256TokenEncoder())
        val pictureDomain = PictureDomain()
        val countriesDomain = CountriesDomain()
        val fridgeDomain = FridgeDomain()

        val testProfilePicture =
            MockMultipartFile(
                "test-profile-picture.jpeg",
                "test-profile-picture.jpeg",
                "image/jpeg",
                FileInputStream("src/test/resources/test-profile-picture.jpeg")
            )

        val testProfilePicture2 = MockMultipartFile(
            "test-profile-picture2.jpeg",
            "test-profile-picture2.jpeg",
            "image/jpg",
            FileInputStream("src/test/resources/test-profile-picture2.jpg")
        )

        private fun getFirestoreService(): Firestore {
            val serviceAccount = FileInputStream(GOOGLE_CLOUD_CREDENTIALS_LOCATION)

            val options = FirestoreOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseId(FIRESTORE_TEST_DATABASE_ID)
                .build()

            return options.service
        }

        private fun getCloudStorageService(): CloudStorage {
            val serviceAccount = FileInputStream(GOOGLE_CLOUD_CREDENTIALS_LOCATION)

            val options = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            return CloudStorage(options.service, GOOGLE_CLOUD_STORAGE_TEST_BUCKET)
        }
    }
}