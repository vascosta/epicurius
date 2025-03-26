package epicurius.services

import epicurius.domain.fridge.Fridge
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class FridgeService(private val tm: TransactionManager) {
    fun getFridge(userId: Int): Fridge {
        val fridge = tm.run {
            it.fridgeRepository.getFridge(userId)
        }

        return fridge ?: Fridge()
    }
}
