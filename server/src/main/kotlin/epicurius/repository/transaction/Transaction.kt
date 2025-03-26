package epicurius.repository.transaction

import TokenRepository
import UserPostgresRepository
import epicurius.repository.FridgePostgresRepository

interface Transaction {
    val userRepository: UserPostgresRepository
    val tokenRepository: TokenRepository
    val fridgeRepository: FridgePostgresRepository
}
