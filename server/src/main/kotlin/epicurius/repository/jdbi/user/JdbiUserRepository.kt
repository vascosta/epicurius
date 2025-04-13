package epicurius.repository.jdbi.user

import epicurius.domain.PagingParams
import epicurius.domain.user.FollowingStatus
import epicurius.domain.user.UpdateUserModel
import epicurius.domain.user.User
import epicurius.repository.jdbi.user.models.SearchUserModel
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiUserRepository(private val handle: Handle) : UserRepository {

    override fun createUser(username: String, email: String, country: String, passwordHash: String) {
        handle.createUpdate(
            """
               INSERT INTO dbo.user(username, email, password_hash, country, privacy, intolerances, diets)
               VALUES (:username, :email, :password_hash, :country, :privacy, :intolerances, :diets)
            """
        )
            .bind("username", username)
            .bind("email", email)
            .bind("password_hash", passwordHash)
            .bind("country", country)
            .bind("privacy", false) // user is created with a public profile
            .bind("intolerances", emptyArray<Int>())
            .bind("diets", emptyArray<Int>())
            .execute()
    }

    override fun getUser(username: String?, email: String?, tokenHash: String?): User? {

        val bindings = mutableMapOf(
            "username" to username,
            "email" to email,
            "token_hash" to tokenHash
        )

        return handle.createQuery(
            """
                SELECT * FROM dbo.user
                WHERE username = :username OR email = :email OR token_hash = :token_hash
            """
        )
            .bindMap(bindings)
            .mapTo<User>()
            .firstOrNull()
    }

    override fun getUsers(partialUsername: String, pagingParams: PagingParams): List<SearchUserModel> {
        return handle.createQuery(
            """
                SELECT username, profile_picture_name
                FROM dbo.user
                WHERE LOWER(username) LIKE LOWER(:partialUsername)
                LIMIT :limit OFFSET :skip
            """
        )
            .bind("partialUsername", "%$partialUsername%")
            .bind("limit", pagingParams.limit)
            .bind("skip", pagingParams.skip)
            .mapTo<SearchUserModel>()
            .list()
    }

    override fun getFollowers(userId: Int): List<SearchUserModel> {
        return handle.createQuery(
            """
                SELECT u.username, u.profile_picture_name
                FROM dbo.user u
                JOIN dbo.followers f ON u.id = f.follower_id
                WHERE f.user_id = :user_id AND f.status = :status
            """
        )
            .bind("user_id", userId)
            .bind("status", FollowingStatus.ACCEPTED.ordinal)
            .mapTo<SearchUserModel>()
            .list()
    }

    override fun getFollowing(userId: Int): List<SearchUserModel> {
        return handle.createQuery(
            """
                SELECT u.username, u.profile_picture_name
                FROM dbo.user u
                JOIN dbo.followers f ON u.id = f.user_id
                WHERE f.follower_id = :user_id AND f.status = :status
            """
        )
            .bind("user_id", userId)
            .bind("status", FollowingStatus.ACCEPTED.ordinal)
            .mapTo<SearchUserModel>()
            .list()
    }

    override fun getFollowRequests(userId: Int): List<SearchUserModel> {
        return handle.createQuery(
            """
                SELECT u.username, u.profile_picture_name
                FROM dbo.user u
                JOIN dbo.followers f ON u.id = f.follower_id
                WHERE f.user_id = :user_id AND f.status = :status
            """
        )
            .bind("user_id", userId)
            .bind("status", FollowingStatus.PENDING.ordinal)
            .mapTo<SearchUserModel>()
            .list()
    }

    override fun updateUser(username: String, userUpdate: UpdateUserModel): User {
        return handle.createQuery(
            """
                UPDATE dbo.user
                SET username = COALESCE(:newUsername, username),
                    email = COALESCE(:email, email),
                    country = COALESCE(:country, country),
                    password_hash = COALESCE(:password_hash, password_hash),
                    privacy = COALESCE(:privacy, privacy),
                    intolerances = COALESCE(:intolerances, intolerances),
                    diets = COALESCE(:diets, diets),
                    profile_picture_name = :profile_picture_name
                WHERE username = :username
                RETURNING *
            """
        )
            .bind("newUsername", userUpdate.username)
            .bind("email", userUpdate.email)
            .bind("country", userUpdate.country)
            .bind("password_hash", userUpdate.passwordHash)
            .bind("privacy", userUpdate.privacy)
            .bind("intolerances", userUpdate.intolerances?.toTypedArray())
            .bind("diets", userUpdate.diets?.toTypedArray())
            .bind("profile_picture_name", userUpdate.profilePictureName)
            .bind("username", username)
            .mapTo<User>()
            .first()
    }

    override fun resetPassword(email: String, passwordHash: String) {
        handle.createUpdate(
            """
                UPDATE dbo.user
                SET password_hash = :password_hash
                WHERE email = :email
            """
        )
            .bind("email", email)
            .bind("password_hash", passwordHash)
            .execute()
    }

    override fun followUser(userId: Int, userIdToFollow: Int, status: Int) {
        handle.createUpdate(
            """
                INSERT INTO dbo.followers(user_id, follower_id, status)
                VALUES (:user_id, :follower_id, :status)
            """
        )
            .bind("user_id", userIdToFollow)
            .bind("follower_id", userId)
            .bind("status", status)
            .execute()
    }

    override fun unfollowUser(userId: Int, userIdToUnfollow: Int) {
        handle.createUpdate(
            """
                DELETE FROM dbo.followers
                WHERE user_id = :user_id AND follower_id = :follower_id
            """
        )
            .bind("user_id", userIdToUnfollow)
            .bind("follower_id", userId)
            .execute()
    }

    override fun cancelFollowRequest(userId: Int, followerId: Int) {
        handle.createUpdate(
            """
                DELETE FROM dbo.followers
                WHERE user_id = :user_id AND follower_id = :follower_id
            """
        )
            .bind("user_id", userId)
            .bind("follower_id", followerId)
            .execute()
    }

    override fun checkIfUserIsLoggedIn(username: String?, email: String?) =
        handle.createQuery(
            """
                SELECT COUNT (*) FROM dbo.user
                WHERE (username = :username OR email = :email) AND token_hash IS NOT NULL
            """
        )
            .bind("username", username)
            .bind("email", email)
            .mapTo<Int>()
            .one() == 1

    override fun checkIfUserIsBeingFollowedBy(userId: Int, followerId: Int) =
        handle.createQuery(
            """
                SELECT COUNT (*) FROM dbo.followers
                WHERE user_id = :user_id AND follower_id = :follower_id AND status = :status
            """
        )
            .bind("user_id", userId)
            .bind("follower_id", followerId)
            .bind("status", FollowingStatus.ACCEPTED.ordinal)
            .mapTo<Int>()
            .one() == 1

    override fun checkIfUserAlreadySentFollowRequest(userId: Int, followerId: Int) =
        handle.createQuery(
            """
                SELECT COUNT (*) FROM dbo.followers
                WHERE user_id = :user_id AND follower_id = :follower_id AND status = :status
            """
        )
            .bind("user_id", userId)
            .bind("follower_id", followerId)
            .bind("status", FollowingStatus.PENDING.ordinal)
            .mapTo<Int>()
            .one() == 1
}
