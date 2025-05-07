package epicurius.http.user

import epicurius.domain.PagingParams
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.FollowRequestType
import epicurius.domain.user.UserProfile
import epicurius.http.user.models.input.LoginInputModel
import epicurius.http.user.models.input.ResetPasswordInputModel
import epicurius.http.user.models.input.SignUpInputModel
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.http.user.models.output.*
import epicurius.http.utils.Uris
import epicurius.services.user.UserService
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Uris.PREFIX)
class UserController(val userService: UserService) {

    @GetMapping(Uris.User.USER)
    fun getUserInfo(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return ResponseEntity.ok().body(GetUserOutputModel(authenticatedUser.user.toUserInfo()))
    }

    @GetMapping(Uris.User.USER_PROFILE)
    fun getUserProfile(
        authenticatedUser: AuthenticatedUser,
        @PathVariable name: String
    ): ResponseEntity<*> {
        return if (name == authenticatedUser.user.name) {
            val userProfilePicture = userService.getProfilePicture(authenticatedUser.user.profilePictureName)
            val followers = userService.getFollowers(authenticatedUser.user.id)
            val following = userService.getFollowing(authenticatedUser.user.id)
            val userProfile = UserProfile(
                authenticatedUser.user.name,
                authenticatedUser.user.country,
                authenticatedUser.user.privacy,
                userProfilePicture,
                followers,
                following,
            )
            ResponseEntity.ok().body(GetUserProfileOutputModel(userProfile))
        } else {
            val userProfile = userService.getUserProfile(name)
            ResponseEntity.ok().body(GetUserProfileOutputModel(userProfile))
        }
    }

    @GetMapping(Uris.User.USERS)
    fun searchUsers(
        authenticatedUser: AuthenticatedUser,
        @RequestParam partialUsername: String,
        @RequestParam skip: Int,
        @RequestParam limit: Int
    ): ResponseEntity<*> {
        val pagingParams = PagingParams(skip, limit)
        val users = userService.searchUsers(authenticatedUser.user.id, partialUsername, pagingParams)
        return ResponseEntity.ok().body(SearchUsersOutputModel(users))
    }

    @GetMapping(Uris.User.USER_INTOLERANCES)
    fun getUserIntolerances(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val intolerances = authenticatedUser.user.intolerances
        return ResponseEntity.ok().body(GetUserIntolerancesOutputModel(intolerances))
    }

    @GetMapping(Uris.User.USER_DIETS)
    fun getUserDiet(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val diets = authenticatedUser.user.diets
        return ResponseEntity.ok().body(GetUserDietsOutputModel(diets))
    }

    @GetMapping(Uris.User.USER_FOLLOWERS)
    fun getUserFollowers(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val followers = userService.getFollowers(authenticatedUser.user.id)
        return ResponseEntity.ok().body(GetUserFollowersOutputModel(followers))
    }

    @GetMapping(Uris.User.USER_FOLLOWING)
    fun getUserFollowing(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val following = userService.getFollowing(authenticatedUser.user.id)
        return ResponseEntity.ok().body(GetUserFollowingOutputModel(following))
    }

    @GetMapping(Uris.User.USER_FOLLOW_REQUESTS)
    fun getUserFollowRequests(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val followRequests = userService.getFollowRequests(authenticatedUser.user.id)
        return ResponseEntity.ok().body(GetUserFollowRequestsOutputModel(followRequests))
    }

    @PostMapping(Uris.User.SIGNUP)
    fun signUp(@Valid @RequestBody body: SignUpInputModel): ResponseEntity<*> {
        userService.createUser(body.name, body.email, body.country, body.password, body.confirmPassword)
        return ResponseEntity.created(Uris.User.userProfile(body.name)).build<Unit>()
    }

    @PostMapping(Uris.User.LOGIN)
    fun login(@Valid @RequestBody body: LoginInputModel): ResponseEntity<*> {
        userService.login(body.name, body.email, body.password)
        return ResponseEntity.noContent().build<Unit>()
    }

    @PostMapping(Uris.User.LOGOUT)
    fun logout(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        userService.logout(authenticatedUser.user.id)
        return ResponseEntity.noContent().build<Unit>()
    }

    @PatchMapping(Uris.User.USER)
    fun updateUser(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: UpdateUserInputModel
    ): ResponseEntity<*> {
        val updatedUserInfo = userService.updateUser(authenticatedUser.user.id, body)
        return ResponseEntity.ok().body(UpdateUserOutputModel(updatedUserInfo))
    }

    @PatchMapping(Uris.User.USER_PICTURE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateUserProfilePicture(
        authenticatedUser: AuthenticatedUser,
        @RequestPart("picture", required = false) picture: MultipartFile?
    ): ResponseEntity<*> {
        val newProfilePicture = userService.updateProfilePicture(
            authenticatedUser.user.id,
            authenticatedUser.user.profilePictureName,
            picture
        )
        return if (newProfilePicture == null) {
            ResponseEntity.noContent().build<Unit>()
        } else {
            ResponseEntity.ok().body(UpdateUserProfilePictureOutputModel(newProfilePicture))
        }
    }

    @PatchMapping(Uris.User.USER_RESET_PASSWORD)
    fun resetUserPassword(
        @Valid @RequestBody body: ResetPasswordInputModel
    ): ResponseEntity<*> {
        userService.resetPassword(body.email, body.newPassword, body.confirmPassword)
        return ResponseEntity.noContent().build<Unit>()
    }

    @PatchMapping(Uris.User.USER_FOLLOW)
    fun follow(authenticatedUser: AuthenticatedUser, @PathVariable name: String): ResponseEntity<*> {
        userService.follow(authenticatedUser.user.id, authenticatedUser.user.name, name)
        return ResponseEntity.noContent().build<Unit>()
    }

    @PatchMapping(Uris.User.USER_FOLLOW_REQUEST)
    fun followRequest(
        authenticatedUser: AuthenticatedUser,
        @PathVariable name: String,
        @RequestParam type: FollowRequestType,
    ): ResponseEntity<*> {
        userService.followRequest(authenticatedUser.user.id, authenticatedUser.user.name, name, type)
        return ResponseEntity.noContent().build<Unit>()
    }

    @DeleteMapping(Uris.User.USER_FOLLOW)
    fun unfollow(authenticatedUser: AuthenticatedUser, @PathVariable name: String): ResponseEntity<*> {
        userService.unfollow(authenticatedUser.user.id, authenticatedUser.user.name, name)
        return ResponseEntity.ok().build<Unit>()
    }
}
