package epicurius.unit.http.recipe

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import java.util.UUID.randomUUID

open class RecipeHttpTest: HttpTest() {

    companion object {
        const val RECIPE_ID = 1
        const val AUTHOR_ID = 1

        val objectMapper = jacksonObjectMapper()

        private val authenticatedUsername = generateRandomUsername()
        private val token = randomUUID().toString()

        val authenticatedUser = AuthenticatedUser(
            User(
                AUTHOR_ID,
                authenticatedUsername,
                generateEmail(authenticatedUsername),
                usersDomain.encodePassword(randomUUID().toString()),
                usersDomain.hashToken(token),
                "PT",
                false,
                listOf(Intolerance.GLUTEN),
                listOf(Diet.GLUTEN_FREE),
                randomUUID().toString()
            ),
            token,
        )

        val createRecipeInfo = CreateRecipeInputModel(
            "Pastel de nata",
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
            instructions = Instructions(
                mapOf(
                    "1" to "Preheat the oven to 200°C.",
                    "2" to "In a bowl, mix the eggs, sugar, flour, and milk.",
                    "3" to "Pour the mixture into pastry shells.",
                    "4" to "Bake for 20 minutes or until golden brown.",
                    "5" to "Let cool before serving."
                )
            )
        )

        val recipePictures = listOf(testPicture)
    }
}