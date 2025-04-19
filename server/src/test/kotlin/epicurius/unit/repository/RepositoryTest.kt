package epicurius.unit.repository

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.storage.StorageOptions
import epicurius.config.CloudStorage
import epicurius.domain.PagingParams
import epicurius.domain.PictureDomain
import epicurius.domain.fridge.ProductInfo
import epicurius.domain.fridge.UpdateProductInfo
import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.firestore.FirestoreManager
import epicurius.unit.EpicuriusUnitTest
import org.springframework.web.multipart.MultipartFile
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
        private val cs = CloudStorageManager(cloudStorage)

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

        // USER
        fun createUser(username: String, email: String, country: String, passwordHash: String) =
            tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

        fun createToken(tokenHash: String, username: String? = null, email: String? = null) =
            tm.run { it.tokenRepository.createToken(tokenHash, username, email) }

        fun getUserByName(username: String) = tm.run { it.userRepository.getUser(username) }
        fun getUserByEmail(email: String) = tm.run { it.userRepository.getUser(email = email) }
        fun getUserByTokenHash(tokenHash: String) = tm.run { it.userRepository.getUser(tokenHash = tokenHash) }

        fun searchUsers(partialUsername: String, pagingParams: PagingParams) =
            tm.run { it.userRepository.searchUsers(partialUsername, pagingParams) }

        fun getProfilePicture(profilePictureName: String) = cs.pictureCloudStorageRepository.getPicture(profilePictureName, PictureDomain.USERS_FOLDER)

        fun getFollowers(userId: Int) = tm.run { it.userRepository.getFollowers(userId) }
        fun getFollowing(userId: Int) = tm.run { it.userRepository.getFollowing(userId) }
        fun getFollowRequests(userId: Int) = tm.run { it.userRepository.getFollowRequests(userId) }

        fun updateUser(username: String, userUpdate: JdbiUpdateUserModel) =
            tm.run {
                it.userRepository.updateUser(
                    username,
                    JdbiUpdateUserModel(
                        userUpdate.name,
                        userUpdate.email,
                        userUpdate.country,
                        userUpdate.passwordHash,
                        userUpdate.privacy,
                        userUpdate.intolerances,
                        userUpdate.diets
                    )
                )
            }

        fun updateProfilePicture(profilePictureName: String, profilePicture: MultipartFile) =
            cs.pictureCloudStorageRepository.updatePicture(profilePictureName, profilePicture, PictureDomain.USERS_FOLDER)

        fun resetPassword(email: String, passwordHash: String) =
            tm.run { it.userRepository.resetPassword(email, passwordHash) }

        fun follow(userId: Int, userIdToFollow: Int, status: Int) =
            tm.run { it.userRepository.followUser(userId, userIdToFollow, status) }

        fun unfollow(userId: Int, userIdToUnfollow: Int) =
            tm.run { it.userRepository.unfollowUser(userId, userIdToUnfollow) }

        fun cancelFollowRequest(userId: Int, userIdToCancelFollowRequest: Int) =
            tm.run { it.userRepository.cancelFollowRequest(userId, userIdToCancelFollowRequest) }

        fun deleteProfilePicture(profilePictureName: String) =
            cs.pictureCloudStorageRepository.deletePicture(profilePictureName, PictureDomain.USERS_FOLDER)

        fun deleteToken(username: String? = null, email: String? = null) =
            tm.run { it.tokenRepository.deleteToken(username, email) }

        fun checkIfUserIsLoggedIn(username: String? = null, email: String? = null) =
            tm.run { it.userRepository.checkIfUserIsLoggedIn(username, email) }

        fun checkIfUserIsBeingFollowedBy(userId: Int, followerId: Int) =
            tm.run { it.userRepository.checkIfUserIsBeingFollowedBy(userId, followerId) }

        fun checkIfUserAlreadySentFollowRequest(userId: Int, followerId: Int) =
            tm.run { it.userRepository.checkIfUserAlreadySentFollowRequest(userId, followerId) }

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
