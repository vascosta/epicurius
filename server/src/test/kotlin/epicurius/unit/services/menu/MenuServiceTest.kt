package epicurius.unit.services.menu

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.unit.services.ServiceTest
import epicurius.utils.generateRandomRecipeIngredients
import epicurius.utils.generateRandomRecipeName
import epicurius.utils.generateRandomUsername
import java.time.LocalDate

open class MenuServiceTest : ServiceTest() {

    companion object {
        val userIntolerances = listOf(Intolerance.EGG)
        val userDiets = listOf(Diet.GLUTEN_FREE)

        val publicBreakfastJdbiRecipeModel = JdbiRecipeInfo(
            1,
            generateRandomRecipeName(),
            Cuisine.MEDITERRANEAN,
            MealType.BREAKFAST,
            1,
            1,
            picturesNames = listOf("")
        )

        val publicSoupJdbiRecipeModel = JdbiRecipeInfo(
            2,
            generateRandomRecipeName(),
            Cuisine.MEDITERRANEAN,
            MealType.SOUP,
            1,
            1,
            picturesNames = listOf("")
        )

        val publicDessertJdbiRecipeModel = JdbiRecipeInfo(
            3,
            generateRandomRecipeName(),
            Cuisine.MEDITERRANEAN,
            MealType.DESSERT,
            1,
            1,
            picturesNames = listOf("")
        )

        val publicLunchJdbiRecipeModel = JdbiRecipeInfo(
            4,
            generateRandomRecipeName(),
            Cuisine.MEDITERRANEAN,
            MealType.MAIN_COURSE,
            1,
            1,
            picturesNames = listOf("")
        )

        val publicDinnerJdbiRecipeModel2 = publicLunchJdbiRecipeModel.copy(
            id = 5,
            name = generateRandomRecipeName(),
        )

        private val breakfastRecipes = listOf(publicBreakfastJdbiRecipeModel.toRecipeInfo(byteArrayOf()))
        private val soupRecipes = listOf(publicSoupJdbiRecipeModel.toRecipeInfo(byteArrayOf()))
        private val dessertRecipes = listOf(publicDessertJdbiRecipeModel.toRecipeInfo(byteArrayOf()))
        private val lunchRecipes = listOf(publicLunchJdbiRecipeModel.toRecipeInfo(byteArrayOf()))
        private val dinnerRecipes = listOf(publicDinnerJdbiRecipeModel2.toRecipeInfo(byteArrayOf()))
        val testDailyMenu = mapOf(
            "breakfast" to breakfastRecipes,
            "soup" to soupRecipes,
            "dessert" to dessertRecipes,
            "lunch" to lunchRecipes,
            "dinner" to dinnerRecipes
        )
    }
}
