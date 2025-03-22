package epicurius.services

import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.CountriesDomain
import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.user.UserDomain
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExits
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.user.models.UpdateUserInputModel
import epicurius.repository.transaction.TransactionManager
import epicurius.repository.transaction.firestore.FirestoreManager
import epicurius.services.models.UpdateUserModel
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

    fun getAuthenticatedUser(token: String): AuthenticatedUser? {
        val tokenHash = userDomain.hashToken(token)
        if (!checkIfUserExists(tokenHash = tokenHash)) return null
        return tm.run {
            val user = it.userRepository.getUserFromTokenHash(tokenHash)
            AuthenticatedUser(user, token)
        }
    }

    fun getFollowers(username: String) {

    }

    fun getFollowing(username: String) {

    }

    fun getFollowingRequests(username: String) {

    }

    fun getProfilePicture(username: String) { }

    fun addProfilePicture() { }

    fun login(username: String?, email: String?, password: String): String {
        if (!checkIfUserExists(username, email)) throw UserNotFound(username)
        if (checkIfUserIsLoggedIn(username, email)) throw UserAlreadyLoggedIn()

        val user = tm.run { it.userRepository.getUser(username, email) }
        if (!userDomain.verifyPassword(password, user.passwordHash)) throw IncorrectPassword()
        return createToken(username, email)
    }

    fun follow(username: String, usernameToFollow: String) {
        fs.userRepository.addFollowing(username, usernameToFollow)
    }

    fun updateProfile(username: String, userUpdate: UpdateUserInputModel) {
        userUpdate.username?.let { if (checkIfUserExists(name = it)) throw UserAlreadyExits() }

        userUpdate.email?.let { if (checkIfUserExists(email = it)) throw UserAlreadyExits() }

        userUpdate.country?.let { if (!countriesDomain.checkIfCodeIsValid(it)) throw InvalidCountry() }

        if (userUpdate.password != null) {
            if (userUpdate.confirmPassword == null ||
                !checkIfPasswordsMatch(userUpdate.password, userUpdate.confirmPassword)
            ) {
                throw PasswordsDoNotMatch()
            }
        }

        tm.run {
            it.userRepository.updateProfile(
                username,
                UpdateUserModel(
                    userUpdate.username,
                    userUpdate.email,
                    userUpdate.country,
                    userUpdate.password?.let { userDomain.encodePassword(it) },
                    userUpdate.privacy,
                    userUpdate.intolerances?.map { intolerance ->  Intolerance.entries.indexOf(intolerance) },
                    userUpdate.diet?.map { diet -> Diet.entries.indexOf(diet) }
                )
            )
        }
    }

    fun updateProfilePicture() { }

    fun resetPassword(username: String, newPassword: String, confirmPassword: String) {
        if (!checkIfPasswordsMatch(newPassword, confirmPassword)) throw PasswordsDoNotMatch()
        val passwordHash = userDomain.encodePassword(newPassword)

        tm.run {
            it.userRepository.resetPassword(username, passwordHash)
        }
    }

    fun removeProfilePicture() { }

    fun logout(username: String) {
        deleteToken(username)
    }

    fun unfollow(username: String, usernameToUnfollow: String) {
        fs.userRepository.removeFollowing(username, usernameToUnfollow)
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