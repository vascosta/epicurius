package epicurius.repository.jdbi

import User
import UserPostgresRepository
import epicurius.domain.Intolerance
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

    override fun resetPassword(username: String, passwordHash: String) {
        handle.createUpdate(
            """
                UPDATE dbo.user
                SET password_hash = :password_hash
                WHERE username = :username
            """
        )
            .bind("username", username)
            .bind("password_hash", passwordHash)
            .execute()
    }

    override fun updateIntolerances(username: String, intolerancesIdx: List<Int>) {
        handle.createUpdate(
            """
                UPDATE dbo.user
                SET intolerances = :intolerances
                WHERE username = :username
            """
        )
            .bind("username", username)
            .bind("intolerances", intolerancesIdx.toTypedArray())
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
}