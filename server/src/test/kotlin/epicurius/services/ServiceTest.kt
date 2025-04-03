package epicurius.services

import epicurius.EpicuriusTest
import epicurius.domain.PagingParams
import epicurius.http.fridge.models.input.OpenProductInputModel
import epicurius.http.fridge.models.input.ProductInputModel
import epicurius.http.fridge.models.input.UpdateProductInputModel
import epicurius.http.user.models.input.UpdateUserInputModel
import org.springframework.web.multipart.MultipartFile

open class ServiceTest : EpicuriusTest() {

    companion object {
        // private val userService = UserService(tm, fs, cs, usersDomain, countriesDomain)
        private val userService = UserService(tm, cs, usersDomain, countriesDomain)
        private val fridgeService = FridgeService(tm, sm, fridgeDomain)

        // USER
        fun createUser(username: String, email: String, country: String, password: String, confirmPassword: String) =
            userService.createUser(username, email, country, password, confirmPassword)

        fun getAuthenticatedUser(token: String) = userService.getAuthenticatedUser(token)

        fun getUserProfile(username: String) = userService.getUserProfile(username)

        fun getProfilePicture(profilePictureName: String) = userService.getProfilePicture(profilePictureName)

        fun getUsers(partialUsername: String, pagingParams: PagingParams) = userService.getUsers(partialUsername, pagingParams)

        fun getFollowers(userId: Int) = userService.getFollowers(userId)

        fun getFollowing(userId: Int) = userService.getFollowing(userId)

        fun getFollowRequests(userId: Int) = userService.getFollowRequests(userId)

        fun login(username: String? = null, email: String? = null, password: String) =
            userService.login(username, email, password)

        fun logout(username: String) = userService.logout(username)

        fun updateUser(username: String, userUpdate: UpdateUserInputModel) =
            userService.updateUser(username, userUpdate)

        fun updateProfilePicture(username: String, profilePictureName: String? = null, profilePicture: MultipartFile) =
            userService.updateProfilePicture(username, profilePictureName, profilePicture)

        fun resetPassword(email: String, newPassword: String, confirmPassword: String) =
            userService.resetPassword(email, newPassword, confirmPassword)

        fun follow(userId: Int, usernameToFollow: String) = userService.follow(userId, usernameToFollow)

        fun unfollow(userId: Int, usernameToUnfollow: String) = userService.unfollow(userId, usernameToUnfollow)

        fun cancelFollowRequest(userId: Int, usernameToCancelFollow: String) =
            userService.cancelFollowRequest(userId, usernameToCancelFollow)

        // FRIDGE
        fun getFridge(userId: Int) = fridgeService.getFridge(userId)

        suspend fun addProduct(userId: Int, productName: ProductInputModel) =
            fridgeService.addProduct(userId, productName)

        suspend fun getProductsList(partial: String) = fridgeService.getProductsList(partial)

        fun updateProductInfo(userId: Int, entryNumber: Int, product: UpdateProductInputModel) =
            fridgeService.updateProductInfo(userId, entryNumber, product)

        fun openProduct(userId: Int, entryNumber: Int, product: OpenProductInputModel) =
            fridgeService.openProduct(userId, entryNumber, product)

        fun removeProduct(userId: Int, entryNumber: Int) = fridgeService.removeProduct(userId, entryNumber)
    }
}
