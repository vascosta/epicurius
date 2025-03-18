package epicurius.repository.transaction

import TokenRepository
import UserPostgresRepository

interface Transaction {
    val userRepository: UserPostgresRepository
    val tokenRepository: TokenRepository
}