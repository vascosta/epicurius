package epicurius.services

import epicurius.EpicuriusTest
import epicurius.domain.PagingParams
import epicurius.domain.user.User
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeAll
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.io.FileInputStream

open class ServicesTest : EpicuriusTest() {

    companion object {
        // private val userService = UserService(tm, fs, cs, usersDomain, countriesDomain)
        private val userService = UserService(tm, cs, usersDomain, countriesDomain)

        lateinit var publicTestUser: User
        lateinit var privateTestUser: User

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

        @JvmStatic
        @BeforeAll
        fun setupDB() {
            publicTestUser = createTestUser(tm)
            privateTestUser = createTestUser(tm)
        }

        fun createUser(username: String, email: String, country: String, password: String, confirmPassword: String) =
            userService.createUser(username, email, country, password, confirmPassword)

        fun getAuthenticatedUser(token: String) = userService.getAuthenticatedUser(token)

        fun getUserProfile(username: String) = userService.getUserProfile(username)

        fun getProfilePicture(profilePictureName: String) = userService.getProfilePicture(profilePictureName)

        fun getUsers(partialUsername: String, pagingParams: PagingParams) = userService.getUsers(partialUsername, pagingParams)

        fun login(username: String?, email: String?, password: String) = userService.login(username, email, password)

        fun logout(username: String) = userService.logout(username)

        fun updateUser(username: String, userUpdate: UpdateUserInputModel) =
            userService.updateUser(username, userUpdate)

        fun updateProfilePicture(username: String, profilePictureName: String? = null, profilePicture: MultipartFile) =
            userService.updateProfilePicture(username, profilePictureName, profilePicture)

        fun resetPassword(email: String, newPassword: String, confirmPassword: String) =
            userService.resetPassword(email, newPassword, confirmPassword)

        fun follow(userId: Int, usernameToFollow: String) = userService.follow(userId, usernameToFollow)
    }
}
