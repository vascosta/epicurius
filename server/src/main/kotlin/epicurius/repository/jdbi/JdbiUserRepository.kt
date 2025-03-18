package epicurius.repository.jdbi

import User
import UserRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiUserRepository(
    private val handle: Handle
) : UserRepository {
    override fun createUser(username: String, email: String, country: String, passwordHash: String): Int {
        val userId = handle.createQuery(
            """
               insert into user(username, email, password_hash, country, privacy)
               values (:username, :email, :password_hash, :country, :privacy)
            """
        )
            .bind("username", username)
            .bind("email", email)
            .bind("password_hash", passwordHash)
            .bind("country", country)
            .bind("privacy", false)
            .mapTo<Int>()
            .one()

        return userId
    }

    override fun getUserFromTokenHash(tokenHash: String): User {
        return handle.createQuery(
            """
                SELECT * FROM users 
                WHERE token_hash = :token_hash
            """
        )
            .bind("token_hash", tokenHash)
            .mapTo<User>()
            .one()
    }

    override fun checkIfUserExists(username: String?, email: String?, tokenHash: String?): Boolean =
        handle.createQuery(
            """
                SELECT COUNT (*) FROM users 
                WHERE username = :username OR email = :email OR token_hash = :token_hash
            """
        )
            .bind("name", username)
            .bind("email", email)
            .bind("token_hash", tokenHash)
            .mapTo<Int>()
            .one() == 1

    override fun checkIfUserIsLoggedIn(username: String?, email: String?): Boolean =
        handle.createQuery(
            """
                SELECT COUNT (*) FROM users 
                WHERE (username = :username OR email = :email) AND token_hash IS NOT NULL
            """
        )
            .bind("username", username)
            .mapTo<Int>()
            .one() == 1
}