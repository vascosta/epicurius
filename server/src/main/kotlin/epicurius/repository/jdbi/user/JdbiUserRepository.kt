package epicurius.repository.jdbi.user

import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowingStatus
import epicurius.domain.user.User
import epicurius.repository.jdbi.user.contract.UserRepository
import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import epicurius.repository.jdbi.user.models.SearchUserModel
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiUserRepository(private val handle: Handle) : UserRepository {

    override fun createUser(name: String, email: String, country: String, passwordHash: String): Int =
        handle.createUpdate(
            """
               INSERT INTO dbo.user(name, email, password_hash, country, privacy, intolerances, diets)
               VALUES (:name, :email, :password_hash, :country, :privacy, :intolerances, :diets)
                RETURNING id
            """
        )
            .bind("name", name)
            .bind("email", email)
            .bind("password_hash", passwordHash)
            .bind("country", country)
            .bind("privacy", false) // user is created with a public profile
            .bind("intolerances", emptyArray<Int>())
            .bind("diets", emptyArray<Int>())
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getUser(name: String?, email: String?, tokenHash: String?): User? {

        val bindings = mutableMapOf(
            "name" to name,
            "email" to email,
            "token_hash" to tokenHash
        )

        return handle.createQuery(
            """
                SELECT u.id, u.name, u.email, u.password_hash, u.country, u.privacy, 
                u.intolerances, u.diets, u.profile_picture_name,
                t.hash as token_hash
                FROM dbo.user u
                LEFT JOIN dbo.token t ON t.user_id = u.id
                WHERE u.name = :name OR u.email = :email OR t.hash = :token_hash
            """
        )
            .bindMap(bindings)
            .mapTo<User>()
            .firstOrNull()
    }

    override fun getUserById(userId: Int) =
        handle.createQuery(
            """
                SELECT u.id, u.name, u.email, u.password_hash, u.country, u.privacy, 
                u.intolerances, u.diets, u.profile_picture_name,
                t.hash as token_hash
                FROM dbo.user u
                LEFT JOIN dbo.token t ON t.user_id = u.id
                WHERE u.id = :userId
            """
        )
            .bind("userId", userId)
            .mapTo<User>()
            .firstOrNull()

    override fun searchUsers(
        userId: Int,
        partialUsername: String,
        lastUserId: Int?,
        limit: Int
    ): List<SearchUserModel> =
        handle.createQuery(
            """
                SELECT id, name, profile_picture_name
                FROM dbo.user
                WHERE LOWER(name) LIKE LOWER(:partialUsername) 
                    AND id <> :userId
                    AND (:lastUserId IS NULL OR id < :lastUserId)
                ORDER BY id DESC
                LIMIT :limit
            """
        )
            .bind("partialUsername", "%$partialUsername%")
            .bind("userId", userId)
            .bind("lastUserId", lastUserId)
            .bind("limit", limit)
            .mapTo<SearchUserModel>()
            .list()

    override fun getFollowers(
        userId: Int,
        partialFollowerName: String?,
        lastFollowerId: Int?,
        limit: Int
    ): List<SearchUserModel> =
        handle.createQuery(
            """
                SELECT u.id, u.name, u.profile_picture_name
                FROM dbo.user u
                JOIN dbo.followers f ON u.id = f.follower_id
                WHERE f.user_id = :user_id 
                    AND (LOWER(u.name) LIKE LOWER(:partialFollowerName) OR :partialFollowerName IS NULL)
                    AND f.status = :status
                    AND (:lastFollowerId IS NULL OR f.follower_id < :lastFollowerId)
                ORDER BY u.id DESC
                LIMIT :limit
            """
        )
            .bind("user_id", userId)
            .bind("partialFollowerName", partialFollowerName)
            .bind("status", FollowingStatus.ACCEPTED.ordinal)
            .bind("lastFollowerId", lastFollowerId)
            .bind("limit", limit)
            .mapTo<SearchUserModel>()
            .list()

    override fun getFollowersCount(userId: Int): Int =
        handle.createQuery(
            """
                SELECT COUNT(*)
                FROM dbo.user u
                JOIN dbo.followers f ON u.id = f.follower_id
                WHERE f.user_id = :user_id AND f.status = :status
            """
        )
            .bind("user_id", userId)
            .bind("status", FollowingStatus.ACCEPTED.ordinal)
            .mapTo<Int>()
            .one()

    override fun getFollowing(
        userId: Int,
        partialFollowingName: String?,
        lastFollowingId: Int?,
        limit: Int
    ): List<SearchUserModel> =
        handle.createQuery(
            """
                SELECT u.id, u.name, u.profile_picture_name
                FROM dbo.user u
                JOIN dbo.followers f ON u.id = f.user_id
                WHERE f.follower_id = :user_id 
                    AND (LOWER(u.name) LIKE LOWER(:partialFollowingName) OR :partialFollowingName IS NULL)
                    AND f.status = :status
                    AND (:lastFollowingId IS NULL OR f.user_id < :lastFollowingId)
                ORDER BY u.id DESC
                LIMIT :limit
            """
        )
            .bind("user_id", userId)
            .bind("partialFollowingName", partialFollowingName)
            .bind("status", FollowingStatus.ACCEPTED.ordinal)
            .bind("lastFollowingId", lastFollowingId)
            .bind("limit", limit)
            .mapTo<SearchUserModel>()
            .list()

    override fun getFollowingCount(userId: Int): Int =
        handle.createQuery(
            """
                SELECT COUNT(*)
                FROM dbo.user u
                JOIN dbo.followers f ON u.id = f.user_id
                WHERE f.follower_id = :user_id AND f.status = :status
            """
        )
            .bind("user_id", userId)
            .bind("status", FollowingStatus.ACCEPTED.ordinal)
            .mapTo<Int>()
            .one()

    override fun getFollowRequests(userId: Int): List<SearchUserModel> =
        handle.createQuery(
            """
                SELECT u.id, u.name, u.profile_picture_name
                FROM dbo.user u
                JOIN dbo.followers f ON u.id = f.follower_id
                WHERE f.user_id = :user_id AND f.status = :status
            """
        )
            .bind("user_id", userId)
            .bind("status", FollowingStatus.PENDING.ordinal)
            .mapTo<SearchUserModel>()
            .list()

    override fun getUserProfilePictureName(userId: Int): String? =
        handle.createQuery(
            """
                SELECT profile_picture_name
                FROM dbo.user
                WHERE id = :userId
            """
        )
            .bind("userId", userId)
            .mapTo<String>()
            .firstOrNull()

    override fun updateUser(userId: Int, userUpdateInfo: JdbiUpdateUserModel): User =
        handle.createQuery(
            """
                WITH updated_user AS (
                    UPDATE dbo.user
                    SET name = COALESCE(:newUsername, name),
                        email = COALESCE(:email, email),
                        country = COALESCE(:country, country),
                        password_hash = COALESCE(:password_hash, password_hash),
                        privacy = COALESCE(:privacy, privacy),
                        intolerances = COALESCE(:intolerances, intolerances),
                        diets = COALESCE(:diets, diets),
                        profile_picture_name = :profile_picture_name
                    WHERE id = :userId
                    RETURNING *
                )
                SELECT u.*, t.hash as token_hash
                FROM updated_user u
                LEFT JOIN dbo.token t ON t.user_id = u.id
            """
        )
            .bind("newUsername", userUpdateInfo.name)
            .bind("email", userUpdateInfo.email)
            .bind("country", userUpdateInfo.country)
            .bind("password_hash", userUpdateInfo.passwordHash)
            .bind("privacy", userUpdateInfo.privacy)
            .bind("intolerances", userUpdateInfo.intolerances?.toTypedArray())
            .bind("diets", userUpdateInfo.diets?.toTypedArray())
            .bind("profile_picture_name", userUpdateInfo.profilePictureName)
            .bind("userId", userId)
            .mapTo<User>()
            .first()

    override fun resetPassword(userId: Int, passwordHash: String) {
        handle.createUpdate(
            """
                UPDATE dbo.user
                SET password_hash = :password_hash
                WHERE id = :userId
            """
        )
            .bind("userId", userId)
            .bind("password_hash", passwordHash)
            .execute()
    }

    override fun follow(userId: Int, userIdToFollow: Int, status: Int) {
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

    override fun unfollow(userId: Int, userIdToUnfollow: Int) {
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

    override fun acceptFollowRequest(userId: Int, followerId: Int) {
        handle.createUpdate(
            """
                UPDATE dbo.followers
                SET status = :status
                WHERE user_id = :user_id AND follower_id = :follower_id
            """
        )
            .bind("user_id", userId)
            .bind("follower_id", followerId)
            .bind("status", FollowingStatus.ACCEPTED.ordinal)
            .execute()
    }

    override fun rejectFollowRequest(userId: Int, followerId: Int) {
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

    override fun deleteUser(userId: Int) {
        handle.createUpdate(
            """
                DELETE FROM dbo.User
                WHERE id = :userId
            """
        )
            .bind("userId", userId)
            .execute()
    }

    override fun checkIfUserIsLoggedIn(userId: Int) =
        handle.createQuery(
            """
                SELECT COUNT (*) FROM dbo.user u
                LEFT JOIN dbo.token t ON t.user_id = u.id
                WHERE u.id = :userId AND t.hash IS NOT NULL
            
            """
        )
            .bind("userId", userId)
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

    override fun checkUserVisibility(username: String, followerId: Int): Boolean {
        val user = getUser(username) ?: throw UserNotFound(username)
        if (user.id == followerId) return true

        return !user.privacy || checkIfUserIsBeingFollowedBy(user.id, followerId)
    }
}
