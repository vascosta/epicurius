package epicurius.unit.repository

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.storage.StorageOptions
import epicurius.config.CloudStorage
import epicurius.domain.fridge.ProductInfo
import epicurius.domain.fridge.UpdateProductInfo
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.firestore.FirestoreManager
import epicurius.unit.EpicuriusUnitTest
import java.io.FileInputStream

open class RepositoryTest : EpicuriusUnitTest() {

    companion object {

/*        init {
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
        }*/

        private const val FIRESTORE_TEST_DATABASE_ID = "epicurius-test-database"
        private const val GOOGLE_CLOUD_STORAGE_TEST_BUCKET = "epicurius-test-bucket"
        private const val GOOGLE_CLOUD_CREDENTIALS_LOCATION = "src/main/resources/epicurius-credentials.json"

        private val firestore = getFirestoreService()
        private val cloudStorage = getCloudStorageService()

        val fs = FirestoreManager(firestore)
        val cs = CloudStorageManager(cloudStorage)

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

        // FRIDGE
        fun getFridge(userId: Int) = tm.run { it.fridgeRepository.getFridge(userId) }

        fun addProduct(userId: Int, product: ProductInfo) = tm.run { it.fridgeRepository.addProduct(userId, product) }

        fun updateProduct(userId: Int, product: UpdateProductInfo) =
            tm.run { it.fridgeRepository.updateProduct(userId, product) }

        fun removeProduct(userId: Int, entryNumber: Int) =
            tm.run { it.fridgeRepository.removeProduct(userId, entryNumber) }

        fun checkIfProductExistsInFridge(userId: Int, entryNumber: Int?, product: ProductInfo?) =
            tm.run { it.fridgeRepository.checkIfProductExistsInFridge(userId, entryNumber, product) }

        fun checkIfProductIsOpen(userId: Int, entryNumber: Int) =
            tm.run { it.fridgeRepository.checkIfProductIsOpen(userId, entryNumber) }
    }
}
