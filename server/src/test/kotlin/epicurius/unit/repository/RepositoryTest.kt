package epicurius.unit.repository

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.storage.StorageOptions
import epicurius.config.CloudStorage
import epicurius.config.HttpClientConfigurer
import epicurius.repository.cloudFunction.manager.CloudFunctionManager
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.firestore.manager.FirestoreManager
import epicurius.repository.jdbi.config.configureWithAppRequirements
import epicurius.repository.spoonacular.manager.SpoonacularManager
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import epicurius.unit.EpicuriusUnitTest
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import java.io.FileInputStream

open class RepositoryTest : EpicuriusUnitTest() {

    companion object {

        private const val POSTGRES_TEST_DATABASE_URL = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"
        private const val FIRESTORE_TEST_DATABASE_ID = "epicurius-test-database"
        private const val GOOGLE_CLOUD_STORAGE_TEST_BUCKET = "epicurius-test-bucket"
        private const val GOOGLE_CLOUD_CREDENTIALS_LOCATION = "src/main/resources/epicurius-credentials.json"

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(POSTGRES_TEST_DATABASE_URL)
            }
        ).configureWithAppRequirements()
        private val firestore = getFirestoreService()
        private val cloudStorage = getCloudStorageService()
        private val httpClient = HttpClientConfigurer()

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

        val tm = JdbiTransactionManager(jdbi)
        val fs = FirestoreManager(firestore)
        val cs = CloudStorageManager(cloudStorage)
        val cf = CloudFunctionManager(httpClient)
        val sm = SpoonacularManager(httpClient)

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
