package epicurius.repository.jdbi

import User
import UserPostgresRepository
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
            .bind("privacy", false)
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
}