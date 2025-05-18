package android.epicurius.services.http.api.user

import android.epicurius.domain.user.FollowRequestType
import android.epicurius.services.http.HttpService
import android.epicurius.services.http.api.user.models.input.ResetPasswordInputModel
import android.epicurius.services.http.api.user.models.input.UpdateUserInputModel
import android.epicurius.services.http.api.user.models.output.GetUserFeedOutputModel
import android.epicurius.services.http.api.user.models.output.GetUserFollowRequestsOutputModel
import android.epicurius.services.http.api.user.models.output.GetUserFollowersOutputModel
import android.epicurius.services.http.api.user.models.output.GetUserFollowingOutputModel
import android.epicurius.services.http.api.user.models.output.GetUserOutputModel
import android.epicurius.services.http.api.user.models.output.GetUserProfileOutputModel
import android.epicurius.services.http.api.user.models.output.UpdateUserOutputModel
import android.epicurius.services.http.api.user.models.output.UpdateUserProfilePictureOutputModel
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.utils.Uris

class UserService(private val httpService: HttpService) {

    suspend fun getUserInfo(
        token: String
    ): APIResult<GetUserOutputModel> =
        httpService.get<GetUserOutputModel>(
            Uris.User.USER,
            token = token
        )

    suspend fun getUserProfile(
        token: String,
        name: String
    ): APIResult<GetUserProfileOutputModel> =
        httpService.get<GetUserProfileOutputModel>(
            Uris.User.USER_PROFILE,
            pathParams = mapOf("name" to name),
            token = token
        )

    suspend fun searchUsers(
        token: String,
        partialUsername: String,
        skip: Int,
        limit: Int,
    ): APIResult<GetUserProfileOutputModel> =
        httpService.get<GetUserProfileOutputModel>(
            Uris.User.USER_PROFILE,
            queryParams = mapOf(
                "name" to partialUsername,
                "skip" to skip,
                "limit" to limit,
            ),
            token = token
        )

    suspend fun getUserFollowers(
        token: String,
        skip: Int,
        limit: Int,
    ): APIResult<GetUserFollowersOutputModel> =
        httpService.get<GetUserFollowersOutputModel>(
            Uris.User.USER_FOLLOWERS,
            queryParams = mapOf(
                "skip" to skip,
                "limit" to limit,
            ),
            token = token
        )

    suspend fun getUserFollowing(
        token: String,
        skip: Int,
        limit: Int,
    ): APIResult<GetUserFollowingOutputModel> =
        httpService.get<GetUserFollowingOutputModel>(
            Uris.User.USER_FOLLOWING,
            queryParams = mapOf(
                "skip" to skip,
                "limit" to limit,
            ),
            token = token
        )

    suspend fun getUserFollowRequests(
        token: String
    ): APIResult<GetUserFollowRequestsOutputModel> =
        httpService.get<GetUserFollowRequestsOutputModel>(
            Uris.User.USER_FOLLOW_REQUESTS,
            token = token
        )

    suspend fun getUserFeed(
        token: String,
        skip: Int,
        limit: Int
    ): APIResult<GetUserFeedOutputModel> =
        httpService.get<GetUserFeedOutputModel>(
            Uris.User.USER_FEED,
            queryParams = mapOf(
                "skip" to skip,
                "limit" to limit,
            ),
            token = token
        )

    suspend fun updateUser(
        token: String,
        updateUserInfo: UpdateUserInputModel
    ): APIResult<UpdateUserOutputModel> =
        httpService.patch<UpdateUserOutputModel>(
            Uris.User.USER,
            body = updateUserInfo,
            token = token
        )

    suspend fun updateUserProfilePicture(
        token: String,
        pictureName: String,
        picture: ByteArray
    ): APIResult<UpdateUserProfilePictureOutputModel> =
        httpService.patchMultipart<UpdateUserProfilePictureOutputModel>(
            Uris.User.USER_PICTURE,
            "picture",
            pictureName,
            picture,
            token = token
        )

    suspend fun resetUserPassword(
        token: String,
        resetPasswordInfo: ResetPasswordInputModel
    ): APIResult<Unit> =
        httpService.patch<Unit>(
            Uris.User.USER_RESET_PASSWORD,
            body = resetPasswordInfo,
            token = token
        )

    suspend fun follow(
        token: String,
        name: String
    ): APIResult<Unit> =
        httpService.patch<Unit>(
            Uris.User.USER_FOLLOW,
            pathParams = mapOf("name" to name),
            token = token
        )

    suspend fun followRequest(
        token: String,
        name: String,
        type: FollowRequestType
    ): APIResult<Unit> =
        httpService.patch<Unit>(
            Uris.User.USER_FOLLOW_REQUEST,
            pathParams = mapOf("name" to name),
            queryParams = mapOf("type" to type),
            token = token
        )

    suspend fun unfollow(
        token: String,
        name: String
    ): APIResult<Unit> =
        httpService.delete<Unit>(
            Uris.User.USER_FOLLOW,
            pathParams = mapOf("name" to name),
            token = token
        )

    suspend fun deleteUser(
        token: String
    ): APIResult<Unit> =
        httpService.delete<Unit>(
            Uris.User.USER,
            token = token
        )
}