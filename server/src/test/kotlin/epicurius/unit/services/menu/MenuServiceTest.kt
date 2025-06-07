package epicurius.unit.services.menu

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.unit.services.ServiceTest
import epicurius.utils.generateRandomRecipeName
import epicurius.utils.generateRandomUsername

open class MenuServiceTest : ServiceTest() {

    companion object {
        val userIntolerances = listOf(Intolerance.EGG)
        val userDiets = listOf(Diet.GLUTEN_FREE)

        val publicBreakfastJdbiRecipeModel = JdbiRecipeInfo(
            1,
            generateRandomRecipeName(),
            generateRandomUsername(),
            0.0,
            Cuisine.MEDITERRANEAN,
            MealType.BREAKFAST,
            1,
            1,
            picturesNames = listOf("")
        )

        val publicSoupJdbiRecipeModel = JdbiRecipeInfo(
            2,
            generateRandomRecipeName(),
            generateRandomUsername(),
            0.0,
            Cuisine.MEDITERRANEAN,
            MealType.SOUP,
            1,
            1,
            picturesNames = listOf("")
        )

        val publicDessertJdbiRecipeModel = JdbiRecipeInfo(
            3,
            generateRandomRecipeName(),
            generateRandomUsername(),
            0.0,
            Cuisine.MEDITERRANEAN,
            MealType.DESSERT,
            1,
            1,
            picturesNames = listOf("")
        )

        val publicLunchJdbiRecipeModel = JdbiRecipeInfo(
            4,
            generateRandomRecipeName(),
            generateRandomUsername(),
            0.0,
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

        private val breakfastRecipes = listOf(publicBreakfastJdbiRecipeModel.toRecipeInfo(byteArrayOf(), false))
        private val soupRecipes = listOf(publicSoupJdbiRecipeModel.toRecipeInfo(byteArrayOf(), false))
        private val dessertRecipes = listOf(publicDessertJdbiRecipeModel.toRecipeInfo(byteArrayOf(), false))
        private val lunchRecipes = listOf(publicLunchJdbiRecipeModel.toRecipeInfo(byteArrayOf(), false))
        private val dinnerRecipes = listOf(publicDinnerJdbiRecipeModel2.toRecipeInfo(byteArrayOf(), false))
        val testDailyMenu = mapOf(
            "breakfast" to breakfastRecipes,
            "soup" to soupRecipes,
            "dessert" to dessertRecipes,
            "lunch" to lunchRecipes,
            "dinner" to dinnerRecipes
        )
    }
}
