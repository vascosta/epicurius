package epicurius.unit.http.menu

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomRecipeName
import epicurius.utils.generateRandomUsername
import java.util.UUID.randomUUID

open class MenuHttpTest: HttpTest() {

    companion object {
        private val authenticatedUsername = generateRandomUsername()
        private val token = randomUUID().toString()
        val testAuthenticatedUser = AuthenticatedUser(
            User(
                1,
                authenticatedUsername,
                generateEmail(authenticatedUsername),
                userDomain.encodePassword(randomUUID().toString()),
                userDomain.hashToken(token),
                "PT",
                false,
                listOf(Intolerance.GLUTEN),
                listOf(Diet.GLUTEN_FREE),
                randomUUID().toString()
            ),
            token,
        )

        val publicBreakfastRecipeInfo = RecipeInfo(
            1,
            generateRandomRecipeName(),
            Cuisine.MEDITERRANEAN,
            MealType.BREAKFAST,
            1,
            2,
            byteArrayOf()
        )

        val publicSoupRecipeInfo = RecipeInfo(
            2,
            generateRandomRecipeName(),
            Cuisine.MEDITERRANEAN,
            MealType.SOUP,
            1,
            2,
            byteArrayOf()
        )

        val publicDessertRecipeInfo = RecipeInfo(
            3,
            generateRandomRecipeName(),
            Cuisine.MEDITERRANEAN,
            MealType.DESSERT,
            1,
            2,
            byteArrayOf()
        )

        val publicLunchJdbiRecipeModel = RecipeInfo(
            4,
            generateRandomRecipeName(),
            Cuisine.MEDITERRANEAN,
            MealType.MAIN_COURSE,
            1,
            2,
            byteArrayOf()
        )

        val publicDinnerJdbiRecipeModel2 = publicLunchJdbiRecipeModel.copy(
            id = 5,
            name = generateRandomRecipeName()
        )

        val testDailyMenu: Map<String, RecipeInfo?> = mapOf(
            "breakfast" to publicBreakfastRecipeInfo,
            "soup" to publicSoupRecipeInfo,
            "dessert" to publicDessertRecipeInfo,
            "lunch" to publicLunchJdbiRecipeModel,
            "dinner" to publicDinnerJdbiRecipeModel2
        )
    }
}