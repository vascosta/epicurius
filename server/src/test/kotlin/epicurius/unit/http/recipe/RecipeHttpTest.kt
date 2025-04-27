package epicurius.unit.http.recipe

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.Recipe
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import java.time.Instant
import java.util.Date
import java.util.UUID.randomUUID

open class RecipeHttpTest : HttpTest() {

    companion object {
        const val RECIPE_ID = 1

        val objectMapper = jacksonObjectMapper()

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

        val recipePictures = setOf(testPicture, testPicture2, testTomatoPicture)

        val testRecipe = Recipe(
            RECIPE_ID,
            "Pastel de nata",
            generateRandomUsername(),
            Date.from(Instant.now()),
            "A delicious Portuguese dessert",
            4,
            30,
            Cuisine.MEDITERRANEAN,
            MealType.DESSERT,
            listOf(Intolerance.EGG, Intolerance.GLUTEN, Intolerance.DAIRY),
            listOf(Diet.OVO_VEGETARIAN, Diet.LACTO_VEGETARIAN),
            listOf(
                Ingredient("Eggs", 4, IngredientUnit.X),
                Ingredient("Sugar", 200, IngredientUnit.G),
                Ingredient("Flour", 100, IngredientUnit.G),
                Ingredient("Milk", 500, IngredientUnit.ML),
                Ingredient("Butter", 50, IngredientUnit.G)
            ),
            300,
            8,
            10,
            40,
            Instructions(
                mapOf(
                    "1" to "Preheat the oven to 200°C.",
                    "2" to "In a bowl, mix the eggs, sugar, flour, and milk.",
                    "3" to "Pour the mixture into pastry shells.",
                    "4" to "Bake for 20 minutes or until golden brown.",
                    "5" to "Let cool before serving."
                )
            ),
            recipePictures.map { it.bytes }
        )
    }
}
