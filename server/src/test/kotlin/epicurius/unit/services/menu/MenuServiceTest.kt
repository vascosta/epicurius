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
        const val PUBLIC_AUTHOR_ID = 1
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
            picturesNames = emptyList()
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
            picturesNames = emptyList()
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
            picturesNames = emptyList()
        )

        val publicMainCourseJdbiRecipeModel = JdbiRecipeModel(
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
            picturesNames = emptyList()
        )

        val publicMainCourseJdbiRecipeModel2 = publicDessertJdbiRecipeModel.copy(
            id = 5,
            name = generateRandomRecipeName(),
            authorId = PUBLIC_AUTHOR_ID,
            authorUsername = publicAuthorUsername
        )

        val breakfastRecipes = listOf(publicBreakfastJdbiRecipeModel)
        val soupRecipes = listOf(publicSoupJdbiRecipeModel)
        val dessertRecipes = listOf(publicDessertJdbiRecipeModel)
        val mainCourseRecipes = listOf(publicMainCourseJdbiRecipeModel, publicMainCourseJdbiRecipeModel2)
        val testDailyMenu = mapOf(
            "breakfast" to breakfastRecipes,
            "soup" to soupRecipes,
            "dessert" to dessertRecipes,
            "lunch" to mainCourseRecipes,
            "dinner" to mainCourseRecipes
        )
    }
}