package epicurius.repository.jdbi

import User
import UserPostgresRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiUserRepository(private val handle: Handle) : UserPostgresRepository {
    override fun createUser(username: String, email: String, country: String, passwordHash: String){
        handle.createQuery(
            """
               INSERT INTO dbo.user(username, email, password_hash, country, privacy)
               VALUES (:username, :email, :password_hash, :country, :privacy)
            """
        )
            .bind("username", username)
            .bind("email", email)
            .bind("password_hash", passwordHash)
            .bind("country", country)
            .bind("privacy", false)
    }

    override fun getUser(username: String?, email: String?): User {
        return handle.createQuery(
            """
                SELECT * FROM dbo.users 
                WHERE username = :username OR email = :email
            """
        )
            .bind("username", username)
            .mapTo<User>()
            .one()
    }

    override fun getUserFromTokenHash(tokenHash: String): User {
        return handle.createQuery(
            """
                SELECT * FROM dbo.users 
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
                SELECT COUNT (*) FROM dbo.users 
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
                SELECT COUNT (*) FROM dbo.users 
                WHERE (username = :username OR email = :email) AND token_hash IS NOT NULL
            """
        )
            .bind("username", username)
            .mapTo<Int>()
            .one() == 1
}