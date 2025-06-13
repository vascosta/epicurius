package epicurius.unit.repository

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.StorageOptions
import epicurius.config.CloudStorage
import epicurius.config.HttpClientConfigurer
import epicurius.repository.cloudFunction.manager.CloudFunctionManager
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.spoonacular.manager.SpoonacularManager
import epicurius.unit.EpicuriusUnitTest
import java.io.FileInputStream

open class RepositoryTest : EpicuriusUnitTest() {

    companion object {
        private const val GOOGLE_CLOUD_STORAGE_TEST_BUCKET = "epicurius-test-bucket"

        private val cloudStorage = getCloudStorageService()
        private val httpClient = HttpClientConfigurer()

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
    }
}
