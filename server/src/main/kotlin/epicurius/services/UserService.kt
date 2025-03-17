import epicurius.domain.UserDomain
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component
import java.util.Locale


@Component
class UserService(
    private val tm: TransactionManager,
    private val domain: UserDomain
) {
    // retornar token, inicio de sessao
    fun createUser(username: String, email: String, country: String, passwordHash: String): Int {
        checkIfUserExists(username, email)
        checkIfCountryIsValid(country)
        val local = Locale("", country)
        val acronym = local.country

        return tm.run {
            return@run it.userRepository.createUser(username, email, acronym, passwordHash)
        }
    }

    fun login(username: String?, email: String?, password: String): String {
        checkIfUserExists(username, email)
        checkIfUserIsLoggedIn(username, email)
        return createToken(username, email)
    }

    private fun createToken(username: String?, email: String?): String {
        checkIfUserExists(username, email)
        checkIfUserIsLoggedIn(username, email)
        val token = domain.generateTokenValue()

        tm.run {
            it.tokenRepository.createToken(token, username, email)
        }
        return token
    }

    private fun checkIfUserExists(name: String?, email: String?) {
        tm.run {
            if (it.userRepository.checkIfUserExists(name, email))
                throw IllegalArgumentException("User already exists")
        }
    }

    private fun checkIfUserIsLoggedIn(username: String?, email: String?) {
        tm.run {
            if (it.userRepository.checkIfUserIsLoggedIn(username, email))
                throw IllegalArgumentException("User already logged in")
        }
    }

    private fun checkIfCountryIsValid(country: String) {
        if (country !in Locale.getISOCountries())
            throw IllegalArgumentException("Invalid country")
    }
}