package epicurius.repository.transaction

import TokenRepository
import UserRepository

interface Transaction {
    val userRepository: UserRepository
    val tokenRepository: TokenRepository
}