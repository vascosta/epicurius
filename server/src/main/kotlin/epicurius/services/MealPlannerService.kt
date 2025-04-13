package epicurius.services

import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component
import java.util.Date

@Component
class MealPlannerService(private val tm: TransactionManager) {

    fun createMealPlanner(userId: Int, date: Date) {
        //checkIfMealPlannerAlreadyExists(userId, date)
        tm.run { it.mealPlannerRepository.createMealPlanner(userId, date) }
    }
}