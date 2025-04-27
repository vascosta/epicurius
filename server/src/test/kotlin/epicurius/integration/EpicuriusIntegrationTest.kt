package epicurius.integration

import epicurius.EpicuriusTest
import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.http.fridge.models.output.FridgeOutputModel
import epicurius.http.user.models.output.GetUserDietsOutputModel
import epicurius.http.user.models.output.GetUserFollowRequestsOutputModel
import epicurius.http.user.models.output.GetUserFollowersOutputModel
import epicurius.http.user.models.output.GetUserFollowingOutputModel
import epicurius.http.user.models.output.GetUserIntolerancesOutputModel
import epicurius.http.user.models.output.GetUserOutputModel
import epicurius.http.user.models.output.GetUserProfileOutputModel
import epicurius.http.user.models.output.SearchUsersOutputModel
import epicurius.http.user.models.output.UpdateUserOutputModel
import epicurius.http.user.models.output.UpdateUserProfilePictureOutputModel
import epicurius.http.utils.Uris
import epicurius.integration.utils.delete
import epicurius.integration.utils.get
import epicurius.integration.utils.getAuthorizationHeader
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.integration.utils.patchMultiPart
import epicurius.integration.utils.post
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters
import java.time.LocalDate
import java.time.Period

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EpicuriusIntegrationTest : EpicuriusTest() {

    @LocalServerPort
    var port: Int = 0
    val client = WebTestClient.bindToServer().baseUrl(api("/")).build()
    final fun api(path: String): String = "http://localhost:$port/api$path"

    // USER
    fun getUser(token: String) = get<GetUserOutputModel>(client, api(Uris.User.USER), token = token)

    fun getUserProfile(token: String, username: String) =
        get<GetUserProfileOutputModel>(
            client,
            api(Uris.User.USER_PROFILE.replace("{username}", username)),
            token = token
        )

    fun getUsers(token: String, partialUsername: String, skip: Int = 0, limit: Int = 10) =
        get<SearchUsersOutputModel>(
            client,
            api(Uris.User.USERS) + "?partialUsername=$partialUsername&skip=$skip&limit=$limit",
            token = token
        )

    fun getIntolerances(token: String) =
        get<GetUserIntolerancesOutputModel>(client, api(Uris.User.USER_INTOLERANCES), token = token)

    fun getDiets(token: String) = get<GetUserDietsOutputModel>(client, api(Uris.User.USER_DIETS), token = token)

    fun getFollowers(token: String) =
        get<GetUserFollowersOutputModel>(client, api(Uris.User.USER_FOLLOWERS), token = token)

    fun getFollowing(token: String) =
        get<GetUserFollowingOutputModel>(client, api(Uris.User.USER_FOLLOWING), token = token)

    fun getFollowRequests(token: String) =
        get<GetUserFollowRequestsOutputModel>(
            client,
            api(Uris.User.USER_FOLLOW_REQUESTS),
            token = token
        )

    fun signUp(username: String, email: String, country: String, password: String): String {
        val result = post<Unit>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "name" to username,
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
        diets: List<Diet>? = null
    ): UpdateUserOutputModel? {
        val result = patch<UpdateUserOutputModel>(
            client,
            api(Uris.User.USER),
            body = mapOf(
                "username" to username,
                "email" to email,
                "country" to country,
                "password" to password,
                "confirmPassword" to confirmPassword,
                "privacy" to privacy,
                "intolerances" to intolerances,
                "diets" to diets
            ),
            responseStatus = HttpStatus.OK,
            token = token
        )

        return getBody(result)
    }

    fun updateProfilePicture(
        token: String,
        profilePicture: MultipartFile
    ): UpdateUserProfilePictureOutputModel? {
        val result = patchMultiPart<UpdateUserProfilePictureOutputModel>(
            client,
            api(Uris.User.USER_PICTURE),
            BodyInserters.fromMultipartData("profilePicture", profilePicture.resource),
            responseStatus = HttpStatus.OK,
            token = token
        )

        return getBody(result)
    }

    fun resetPassword(email: String, newPassword: String, confirmPassword: String) {
        patch<Unit>(
            client,
            api(Uris.User.USER_RESET_PASSWORD),
            body = mapOf(
                "email" to email,
                "newPassword" to newPassword,
                "confirmPassword" to confirmPassword
            )
        )
    }

    fun follow(token: String, username: String) {
        patch<Unit>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{username}", username)),
            body = "",
            token = token
        )
    }

    fun unfollow(token: String, username: String) {
        delete<Unit>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{username}", username)),
            token = token
        )
    }

    fun cancelFollowRequest(token: String, username: String) {
        patch<Unit>(
            client,
            api(Uris.User.USER_FOLLOW_REQUEST.replace("{username}", username) + "?type=CANCEL"),
            body = "",
            token = token
        )
    }

    // FRIDGE
    fun getFridge(token: String) = get<FridgeOutputModel>(client, api(Uris.Fridge.FRIDGE), token = token)

    fun getProductsList(token: String, partial: String) =
        get<List<String>>(client, api(Uris.Fridge.PRODUCTS) + "?partial=$partial", token = token)

    fun addProducts(token: String, productName: String, quantity: Int, openDate: LocalDate? = null, expirationDate: LocalDate) =
        post<FridgeOutputModel>(
            client,
            api(Uris.Fridge.FRIDGE),
            mapOf(
                "productName" to productName,
                "quantity" to quantity,
                "openDate" to openDate,
                "expirationDate" to expirationDate
            ),
            HttpStatus.OK,
            token = token
        )

    fun updateFridgeProduct(token: String, entryNumber: Int, quantity: Int? = null, expirationDate: LocalDate? = null) =
        patch<FridgeOutputModel>(
            client,
            api(Uris.Fridge.PRODUCT.take(16) + entryNumber),
            body = mapOf("quantity" to quantity, "expirationDate" to expirationDate),
            responseStatus = HttpStatus.OK,
            token = token
        )

    fun openFridgeProduct(
        token: String,
        entryNumber: Int,
        openDate: LocalDate,
        duration: Period
    ) = patch<FridgeOutputModel>(
        client,
        api(Uris.Fridge.PRODUCT.take(16) + entryNumber),
        body = mapOf("openDate" to openDate, "duration" to duration),
        responseStatus = HttpStatus.OK,
        token = token
    )

    fun removeProduct(token: String, entryNumber: Int) =
        delete<FridgeOutputModel>(
            client,
            api(Uris.Fridge.PRODUCT.take(16) + entryNumber),
            HttpStatus.OK,
            token = token
        )
}
