package epicurius.repository.jdbi

import UserPostgresRepository
import epicurius.domain.user.User
import epicurius.services.models.UpdateUserModel
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiUserRepository(private val handle: Handle) : UserPostgresRepository {
    override fun createUser(username: String, email: String, country: String, passwordHash: String){
        handle.createUpdate(
            """
               INSERT INTO dbo.user(username, email, password_hash, country, privacy, intolerances, diet)
               VALUES (:username, :email, :password_hash, :country, :privacy, :intolerances, :diet)
            """
        )
            .bind("username", username)
            .bind("email", email)
            .bind("password_hash", passwordHash)
            .bind("country", country)
            .bind("privacy", false) // user is created with a public profile
            .bind("intolerances", emptyArray<Int>())
            .bind("diet", emptyArray<Int>())
            .execute()
    }

    override fun getUser(username: String?, email: String?): User {
        return handle.createQuery(
            """
                SELECT * FROM dbo.user
                WHERE username = :username OR email = :email
            """
        )
            .bind("username", username)
            .bind("email", email)
            .mapTo<User>()
            .one()
    }

    override fun getUserFromTokenHash(tokenHash: String): User {
        return handle.createQuery(
            """
                SELECT * FROM dbo.user
                WHERE token_hash = :token_hash
            """
        )
            .bind("token_hash", tokenHash)
            .mapTo<User>()
            .one()
    }

    override fun getProfilePictureName(username: String): String {
        return handle.createQuery(
            """
                SELECT profile_picture_name
                FROM dbo.user
                WHERE username = :username
            """
        )
            .bind("username", username)
            .mapTo<String>()
            .one()
    }

    override fun updateProfile(username: String, userUpdate: UpdateUserModel) {
        val bindings = getBindingMap(userUpdate)

        val sqlQuery = getUpdateUserQuery(bindings)

        val updateQuery =
            handle.createUpdate(sqlQuery)
                .bind("previousUsername", username)

        bindings.forEach { (key, value) -> updateQuery.bind(key, value) }

        updateQuery.execute()
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

    override fun checkIfUserExists(username: String?, email: String?, tokenHash: String?): Boolean =
        handle.createQuery(
            """
                SELECT COUNT (*) FROM dbo.user
                WHERE username = :username OR email = :email OR token_hash = :token_hash
            """
        )
            .bind("username", username)
            .bind("email", email)
            .bind("token_hash", tokenHash)
            .mapTo<Int>()
            .one() == 1

    override fun checkIfUserIsLoggedIn(username: String?, email: String?): Boolean =
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

    private fun getBindingMap(userUpdate: UpdateUserModel): Map<String, Any> {
        val bindings = mutableMapOf<String, Any>()

        userUpdate.username?.let { bindings["username"] = it }
        userUpdate.email?.let { bindings["email"] = it }
        userUpdate.country?.let {  bindings["country"] = it }
        userUpdate.password?.let { bindings["password_hash"] = it }
        userUpdate.privacy?.let { bindings["privacy"] = it }
        userUpdate.intolerances?.let { bindings["intolerances"] = it.toTypedArray() }
        userUpdate.diet?.let { bindings["diet"] = it.toTypedArray() }

        return bindings
    }

    private fun getUpdateUserQuery(bindingsMap: Map<String, Any>): String {
        val updates = bindingsMap.entries.map { bindingName -> "${bindingName.key} = :${bindingName.key}" }

        return """
                UPDATE dbo.user 
                SET ${updates.joinToString(", ")} 
                WHERE username = :previousUsername
               """
    }
}