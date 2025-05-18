package epicurius.integration.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.user.FollowRequestType
import epicurius.http.controllers.user.models.output.GetUserDietsOutputModel
import epicurius.http.controllers.user.models.output.GetUserFollowRequestsOutputModel
import epicurius.http.controllers.user.models.output.GetUserFollowersOutputModel
import epicurius.http.controllers.user.models.output.GetUserFollowingOutputModel
import epicurius.http.controllers.user.models.output.GetUserIntolerancesOutputModel
import epicurius.http.controllers.user.models.output.GetUserOutputModel
import epicurius.http.controllers.user.models.output.GetUserProfileOutputModel
import epicurius.http.controllers.user.models.output.SearchUsersOutputModel
import epicurius.http.controllers.user.models.output.UpdateUserOutputModel
import epicurius.http.controllers.user.models.output.UpdateUserProfilePictureOutputModel
import epicurius.http.utils.Uris
import epicurius.integration.EpicuriusIntegrationTest
import epicurius.integration.utils.delete
import epicurius.integration.utils.get
import epicurius.integration.utils.getBody
import epicurius.integration.utils.getCookieHeader
import epicurius.integration.utils.patch
import epicurius.integration.utils.patchMultiPart
import epicurius.integration.utils.post
import org.springframework.http.HttpStatus
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters

class UserIntegrationTest: EpicuriusIntegrationTest() {

    fun getUserInfo(token: String) = get<GetUserOutputModel>(client, api(Uris.User.USER), token = token)

    fun getUserProfile(token: String, username: String) =
        get<GetUserProfileOutputModel>(
            client,
            api(Uris.User.USER_PROFILE.replace("{name}", username)),
            token = token
        )

    fun searchUsers(token: String, partialUsername: String, skip: Int = 0, limit: Int = 10) =
        get<SearchUsersOutputModel>(
            client,
            api(Uris.User.USERS) + "?partialUsername=$partialUsername&skip=$skip&limit=$limit",
            token = token
        )

    fun getUserIntolerances(token: String) =
        get<GetUserIntolerancesOutputModel>(client, api(Uris.User.USER_INTOLERANCES), token = token)

    fun getUserDiets(token: String) = get<GetUserDietsOutputModel>(client, api(Uris.User.USER_DIETS), token = token)

    fun getUserFollowers(token: String) =
        get<GetUserFollowersOutputModel>(client, api(Uris.User.USER_FOLLOWERS), token = token)

    fun getUserFollowing(token: String) =
        get<GetUserFollowingOutputModel>(client, api(Uris.User.USER_FOLLOWING), token = token)

    fun getUserFollowRequests(token: String) =
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

        return getCookieHeader(result)
    }

    fun login(username: String? = null, email: String? = null, password: String): String {
        val result = post<Unit>(
            client,
            api(Uris.User.LOGIN),
            mapOf("name" to username, "email" to email, "password" to password),
        )

        return getCookieHeader(result)
    }

    fun logout(token: String): String {
        val result = post<Unit>(client, api(Uris.User.LOGOUT), "", token = token)
        return getCookieHeader(result)
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
                "name" to username,
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

    fun updateUserProfilePicture(
        token: String,
        picture: MultipartFile,
    ): UpdateUserProfilePictureOutputModel {
        val multipartBody = MultipartBodyBuilder().apply {
            part("picture", picture.resource)
        }.build()

        val result = patchMultiPart<UpdateUserProfilePictureOutputModel>(
            client,
            api(Uris.User.USER_PICTURE),
            BodyInserters.fromMultipartData(multipartBody),
            responseStatus = HttpStatus.OK,
            token = token
        )

        return getBody(result)
    }

    fun removeUserProfilePicture(
        token: String,
    ) {
        val multipartBody = MultipartBodyBuilder().build()

        patchMultiPart<UpdateUserProfilePictureOutputModel>(
            client,
            api(Uris.User.USER_PICTURE),
            BodyInserters.fromMultipartData("picture", multipartBody),
            responseStatus = HttpStatus.NO_CONTENT,
            token = token
        )
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
            api(Uris.User.USER_FOLLOW.replace("{name}", username)),
            body = "",
            token = token
        )
    }

    fun unfollow(token: String, username: String) {
        delete<Unit>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{name}", username)),
            token = token
        )
    }

    fun cancelFollowRequest(token: String, username: String) {
        patch<Unit>(
            client,
            api(Uris.User.USER_FOLLOW_REQUEST.replace("{name}", username) + "?type=${FollowRequestType.CANCEL}"),
            body = "",
            token = token
        )
    }

    fun deleteUser(token: String): String {
        val result =  delete<Unit>(
            client,
            api(Uris.User.USER),
            token = token
        )
        return getCookieHeader(result)
    }
}