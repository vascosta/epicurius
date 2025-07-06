package android.epicurius.ui.screens.mealPlanner.search

import android.content.Context
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.mealPlanner.models.input.AddRecipeToMealPlannerInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class MealPlannerSearchViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    fun addRecipeToMealPlanner(
        date: LocalDate,
        recipeId: Int,
        mealTime: MealTime,
        onSuccessNavigateTo: () -> Unit
    ) {
        disableButtons()
        val addRecipeToMealPlannerInfo = AddRecipeToMealPlannerInputModel(recipeId, mealTime)
        viewModelScope.launch {
            handleAddRecipeToMealPlanner(date, addRecipeToMealPlannerInfo, onSuccessNavigateTo)
        }
    }

    private suspend fun handleAddRecipeToMealPlanner(
        date: LocalDate,
        addRecipeToMealPlannerInfo: AddRecipeToMealPlannerInputModel,
        onSuccessNavigateTo: () -> Unit
    ) {
        val result = request {
            val token = session.getToken()
            service.mealPlannerService.addRecipeToDailyMealPlanner(token, date, addRecipeToMealPlannerInfo)
        }
        when {
            result.isSuccess -> onSuccessNavigateTo()
        }
        enableButtons()
    }
}