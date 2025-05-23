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
import epicurius.repository.spoonacular.manager.SpoonacularManager
import epicurius.unit.EpicuriusUnitTest
import java.io.FileInputStream

open class RepositoryTest : EpicuriusUnitTest() {

    companion object {
        private const val GOOGLE_CLOUD_STORAGE_TEST_BUCKET = "epicurius-test-bucket"
        private const val FIRESTORE_TEST_DATABASE_ID = "epicurius-test-database"

        private val cloudStorage = getCloudStorageService()
        private val httpClient = HttpClientConfigurer()
        private val firestore = getFirestoreService()

        val fs = FirestoreManager(firestore)
        val cs = CloudStorageManager(cloudStorage)
        val cf = CloudFunctionManager(httpClient)
        val sm = SpoonacularManager(httpClient)

        private fun getCloudStorageService(): CloudStorage {
            val serviceAccount = FileInputStream(GOOGLE_CLOUD_CREDENTIALS_LOCATION)

            val options = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            return CloudStorage(options.service, GOOGLE_CLOUD_STORAGE_TEST_BUCKET)
        }

        private fun getFirestoreService(): Firestore {
            val serviceAccount = FileInputStream(GOOGLE_CLOUD_CREDENTIALS_LOCATION)

            val options = FirestoreOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseId(FIRESTORE_TEST_DATABASE_ID)
                .build()

            return options.service
        }
    }
}
