package epicurius.services

import epicurius.domain.AuthenticatedUser
import epicurius.domain.CountriesDomain
import epicurius.domain.Intolerance
import epicurius.domain.UserDomain
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExits
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import epicurius.repository.transaction.TransactionManager
import epicurius.repository.transaction.firestore.FirestoreManager
import org.springframework.stereotype.Component

@Component
class UserService(
    private val tm: TransactionManager,
    private val fs: FirestoreManager,
    private val userDomain: UserDomain,
    private val countriesDomain: CountriesDomain
) {
    fun createUser(username: String, email: String, country: String, password: String): String {
        if (checkIfUserExists(username, email)) throw UserAlreadyExits()
        if (!countriesDomain.checkIfCodeIsValid(country)) throw InvalidCountry()
        val passwordHash = userDomain.encodePassword(password)

        tm.run { it.userRepository.createUser(username, email, country, passwordHash) }
        fs.userRepository.createUserFollowersAndFollowing(username, false)

        return createToken(username, email)
    }

    fun login(username: String?, email: String?, password: String): String {
        if (!checkIfUserExists(username, email)) throw UserNotFound(username)
        if (checkIfUserIsLoggedIn(username, email)) throw UserAlreadyLoggedIn()

        val user = tm.run { it.userRepository.getUser(username, email) }
        if (!userDomain.verifyPassword(password, user.passwordHash)) throw IncorrectPassword()
        return createToken(username, email)
    }

    fun logout(username: String) {
        deleteToken(username)
    }

    fun follow(username: String, usernameToFollow: String) {
        fs.userRepository.addFollowing(username, usernameToFollow)
    }

    fun unfollow(username: String, usernameToUnfollow: String) {
        fs.userRepository.removeFollowing(username, usernameToUnfollow)
    }

    fun resetPassword(username: String, newPassword: String, confirmPassword: String) {
        if (!checkIfPasswordsMatch(newPassword, confirmPassword)) throw PasswordsDoNotMatch()
        val passwordHash = userDomain.encodePassword(newPassword)

        tm.run {
            it.userRepository.resetPassword(username, passwordHash)
        }
    }

    fun addIntolerances(username: String, intolerances: List<Intolerance>) {
        val intolerancesIdx = intolerances.map { Intolerance.entries.indexOf(it) }

        tm.run {
            it.userRepository.addIntolerances(username, intolerancesIdx)
        }
    }

    fun getIntolerances(username: String): List<Intolerance> = tm.run { it.userRepository.getIntolerances(username) }

    fun getAuthenticatedUser(token: String): AuthenticatedUser? {
        val tokenHash = userDomain.hashToken(token)
        if (!checkIfUserExists(tokenHash = tokenHash)) return null
        return tm.run {
            val user = it.userRepository.getUserFromTokenHash(tokenHash)
            AuthenticatedUser(user, token)
        }
    }

    private fun createToken(username: String?, email: String?): String {
        if (!checkIfUserExists(username, email)) throw UserNotFound(username)
        if (checkIfUserIsLoggedIn(username, email)) throw UserAlreadyLoggedIn()

        val token = userDomain.generateTokenValue()
        val tokenHash = userDomain.hashToken(token)
        tm.run { it.tokenRepository.createToken(tokenHash, username, email) }
        return token
    }

    private fun deleteToken(username: String) {
        tm.run { it.tokenRepository.deleteToken(username) }
    }

    private fun checkIfUserExists(name: String? = null, email: String? = null, tokenHash: String? = null): Boolean =
        tm.run { it.userRepository.checkIfUserExists(name, email, tokenHash) }

    private fun checkIfUserIsLoggedIn(username: String?, email: String?): Boolean =
        tm.run { it.userRepository.checkIfUserIsLoggedIn(username, email) }

    private fun checkIfPasswordsMatch(password: String, confirmPassword: String): Boolean = password == confirmPassword
}