package epicurius.unit.services.mealPlanner

import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.repository.jdbi.mealPlanner.models.JdbiMealPlanner
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.whenever
import kotlin.test.Test

class GetWeeklyMealPlannerServiceTests : MealPlannerServiceTest() {

    @Test
    fun `Should get the weekly meal planner successfully`() {
        // given a user (USER_ID)
        val jdbiMealPlanner = JdbiMealPlanner(listOf(jdbiDailyMealPlannerToday, jdbiDailyMealPlannerTomorrow))

        // mock
        whenever(jdbiMealPlannerRepositoryMock.getWeeklyMealPlanner(USER_ID)).thenReturn(jdbiMealPlanner)
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when the user gets the weekly meal planner
        val mealPlanner = getWeeklyMealPlanner(USER_ID)

        // then the weekly meal planner should be returned
        assertEquals(2, mealPlanner.planner.size)
        assertEquals(today, mealPlanner.planner[0].date)
        assertEquals(CALORIES, mealPlanner.planner[0].maxCalories)
        assertEquals(mealTime, mealPlanner.planner[0].meals.keys.first())
        assertEquals(jdbiRecipeInfo.name, mealPlanner.planner[0].meals[mealTime]?.name)
        assertEquals(jdbiRecipeInfo.servings, mealPlanner.planner[0].meals[mealTime]?.servings)
        assertEquals(jdbiRecipeInfo.preparationTime, mealPlanner.planner[0].meals[mealTime]?.preparationTime)
        assertEquals(jdbiRecipeInfo.cuisine, mealPlanner.planner[0].meals[mealTime]?.cuisine)
        assertEquals(jdbiRecipeInfo.mealType, mealPlanner.planner[0].meals[mealTime]?.mealType)
        assertEquals(tomorrow, mealPlanner.planner[1].date)
        assertEquals(CALORIES, mealPlanner.planner[1].maxCalories)
        assertEquals(mealTime, mealPlanner.planner[1].meals.keys.first())
        assertEquals(jdbiRecipeInfo.name, mealPlanner.planner[1].meals[mealTime]?.name)
        assertEquals(jdbiRecipeInfo.servings, mealPlanner.planner[1].meals[mealTime]?.servings)
        assertEquals(jdbiRecipeInfo.preparationTime, mealPlanner.planner[1].meals[mealTime]?.preparationTime)
        assertEquals(jdbiRecipeInfo.cuisine, mealPlanner.planner[1].meals[mealTime]?.cuisine)
        assertEquals(jdbiRecipeInfo.mealType, mealPlanner.planner[1].meals[mealTime]?.mealType)
    }
}
