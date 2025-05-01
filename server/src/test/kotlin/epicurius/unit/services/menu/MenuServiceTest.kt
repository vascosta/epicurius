package epicurius.unit.services.menu

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.unit.services.ServiceTest
import epicurius.utils.generateRandomRecipeIngredients
import epicurius.utils.generateRandomRecipeName
import epicurius.utils.generateRandomUsername
import java.time.LocalDate

open class MenuServiceTest: ServiceTest() {

    companion object {
        private const val PUBLIC_AUTHOR_ID = 1
        val publicAuthorUsername = generateRandomUsername()
        val userIntolerances = listOf(Intolerance.EGG)
        val userDiets = listOf(Diet.GLUTEN_FREE)

        val publicBreakfastJdbiRecipeModel = JdbiRecipeModel(
            1,
            generateRandomRecipeName(),
            PUBLIC_AUTHOR_ID,
            publicAuthorUsername,
            LocalDate.now(),
            1,
            1,
            Cuisine.MEDITERRANEAN,
            MealType.BREAKFAST,
            emptyList(),
            emptyList(),
            generateRandomRecipeIngredients(),
            picturesNames = listOf("")
        )

        val publicSoupJdbiRecipeModel = JdbiRecipeModel(
            2,
            generateRandomRecipeName(),
            PUBLIC_AUTHOR_ID,
            publicAuthorUsername,
            LocalDate.now(),
            1,
            1,
            Cuisine.MEDITERRANEAN,
            MealType.SOUP,
            emptyList(),
            emptyList(),
            generateRandomRecipeIngredients(),
            picturesNames = listOf("")
        )

        val publicDessertJdbiRecipeModel = JdbiRecipeModel(
            3,
            generateRandomRecipeName(),
            PUBLIC_AUTHOR_ID,
            publicAuthorUsername,
            LocalDate.now(),
            1,
            1,
            Cuisine.MEDITERRANEAN,
            MealType.DESSERT,
            emptyList(),
            emptyList(),
            generateRandomRecipeIngredients(),
            picturesNames = listOf("")
        )

        val publicLunchJdbiRecipeModel = JdbiRecipeModel(
            4,
            generateRandomRecipeName(),
            PUBLIC_AUTHOR_ID,
            publicAuthorUsername,
            LocalDate.now(),
            1,
            1,
            Cuisine.MEDITERRANEAN,
            MealType.MAIN_COURSE,
            emptyList(),
            emptyList(),
            generateRandomRecipeIngredients(),
            picturesNames = listOf("")
        )

        val publicDinnerJdbiRecipeModel2 = publicLunchJdbiRecipeModel.copy(
            id = 5,
            name = generateRandomRecipeName(),
            authorId = PUBLIC_AUTHOR_ID,
            authorUsername = publicAuthorUsername
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