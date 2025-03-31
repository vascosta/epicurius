package epicurius.http

import epicurius.EpicuriusTest
import epicurius.domain.Intolerance
import epicurius.domain.user.User
import epicurius.http.user.models.output.GetDietsOutputModel
import epicurius.http.user.models.output.GetFollowRequestsOutputModel
import epicurius.http.user.models.output.GetFollowersOutputModel
import epicurius.http.user.models.output.GetFollowingOutputModel
import epicurius.http.user.models.output.GetIntolerancesOutputModel
import epicurius.http.user.models.output.GetUserOutputModel
import epicurius.http.user.models.output.GetUserProfileOutputModel
import epicurius.http.user.models.output.GetUsersOutputModel
import epicurius.http.user.models.output.UpdateProfilePictureOutputModel
import epicurius.http.user.models.output.UpdateUserOutputModel
import epicurius.http.utils.Uris
import epicurius.http.utils.get
import epicurius.http.utils.getAuthorizationHeader
import epicurius.http.utils.getBody
import epicurius.http.utils.post
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.multipart.MultipartFile

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpTest : EpicuriusTest() {

    @LocalServerPort
    var port: Int = 0
    val client = WebTestClient.bindToServer().baseUrl(api("/")).build()
    final fun api(path: String): String = "http://localhost:$port/api$path"

    fun getUser(token: String) = get<GetUserOutputModel>(client, api(Uris.User.USER), token = token)

    fun getUserProfile(token: String, username: String = "") =
        get<GetUserProfileOutputModel>(
            client,
            api(Uris.User.USER_PROFILE + "?username=$username"),
            token = token
        )

    fun getUsers(token: String, partialUsername: String, skip: Int = 0, limit: Int = 10) =
        get<GetUsersOutputModel>(
            client,
            api(Uris.User.USERS) + "?partialUsername=$partialUsername&skip=$skip&limit=$limit",
            token = token
        )

    fun getIntolerances(token: String) =
        get<GetIntolerancesOutputModel>(client, api(Uris.User.USER_INTOLERANCES), token = token)

    fun getDiets(token: String) = get<GetDietsOutputModel>(client, api(Uris.User.USER_DIETS), token = token)

    fun getFollowers(token: String) =
        get<GetFollowersOutputModel>(client, api(Uris.User.USER_FOLLOWERS), token = token)

    fun getFollowing(token: String) =
        get<GetFollowingOutputModel>(client, api(Uris.User.USER_FOLLOWING), token = token)

    fun getFollowRequests(token: String) =
        get<GetFollowRequestsOutputModel>(client, api(Uris.User.USER_FOLLOW_REQUESTS), token = token)

    fun signUp(username: String, email: String, country: String, password: String): String {
        val result = post<Unit>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to username,
                "email" to email,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            responseStatus = HttpStatus.CREATED
        )

        return getAuthorizationHeader(result)
    }

    fun login(username: String? = null, email: String? = null, password: String): String {
        val result = post<Unit>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to username, "email" to email, "password" to password),
        )

        return getAuthorizationHeader(result)
    }

    fun logout(token: String): String {
        val result = post<Unit>(client, api(Uris.User.LOGOUT), "", token = token)
        return getAuthorizationHeader(result)
    }

    fun updateUser(
        token: String,
        username: String? = null,
        email: String? = null,
        country: String? = null,
        password: String? = null,
        confirmPassword: String? = null,
        privacy: Boolean? = null,
        intolerances: List<Intolerance>? = null,
        diets: List<String>? = null
    ): UpdateUserOutputModel? {
        val result = post<UpdateUserOutputModel>(
            client,
            api(Uris.User.USER),
            mapOf(
                "username" to username,
                "email" to email,
                "country" to country,
                "password" to password,
                "confirmPassword" to confirmPassword,
                "privacy" to privacy,
                "intolerances" to intolerances,
                "diets" to diets
            ),
            HttpStatus.OK,
            token
        )

        return getBody(result)
    }

    fun updateProfilePicture(
        token: String,
        profilePicture: MultipartFile
    ): UpdateProfilePictureOutputModel? {
        val result = post<UpdateProfilePictureOutputModel>(
            client,
            api(Uris.User.USER_PROFILE_PICTURE),
            mapOf("profilePicture" to profilePicture),
            responseStatus = HttpStatus.OK,
            token = token
        )

        return getBody(result)
    }

    fun resetPassword(email: String, newPassword: String, confirmPassword: String) {
        post<Unit>(
            client,
            api(Uris.User.USER_RESET_PASSWORD),
            mapOf(
                "email" to email,
                "newPassword" to newPassword,
                "confirmPassword" to confirmPassword
            )
        )
    }

    fun follow(token: String, username: String) {
        post<Unit>(client, api(Uris.User.USER_FOLLOW), mapOf("username" to username), token = token)
    }

    fun unfollow(token: String, username: String) {
        post<Unit>(client, api(Uris.User.USER_UNFOLLOW), mapOf("username" to username), token = token)
    }

    fun cancelFollowRequest(token: String, username: String) {
        post<Unit>(client, api(Uris.User.USER_FOLLOW_REQUESTS), mapOf("username" to username), token = token)
    }

    companion object {

        lateinit var publicTestUser: User
        lateinit var privateTestUser: User

        @JvmStatic
        @BeforeAll
        fun setupDB() {
            publicTestUser = createTestUser(tm)
            privateTestUser = createTestUser(tm, true)
        }
    }
}
