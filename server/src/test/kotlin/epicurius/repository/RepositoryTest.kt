package epicurius.repository

import epicurius.domain.UserDomain
import epicurius.domain.token.Sha256TokenEncoder
import epicurius.repository.jdbi.JdbiUserRepository
import epicurius.repository.jdbi.utils.configure
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

open class RepositoryTest {
    private val jdbi = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL("")
        }
    ).configure()

    private val handle = jdbi.open()
    private val tokenEncoder = Sha256TokenEncoder()
    private val passwordEncoder = BCryptPasswordEncoder()

    val usersRepository = JdbiUserRepository(handle)

    val userDomain = UserDomain(passwordEncoder, tokenEncoder)

    companion object {
    }
}