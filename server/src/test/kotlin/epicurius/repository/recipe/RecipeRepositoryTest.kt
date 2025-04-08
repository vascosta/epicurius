package epicurius.repository.recipe

import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.repository.RepositoryTest
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.utils.createTestUser
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.Date
import java.util.UUID

class RecipeRepositoryTest : RepositoryTest() {

    @Test
    fun `Test create recipe`() {
        val user = createTestUser(tm)
        val pictureName = UUID.randomUUID().toString()

        val recipe = CreateRecipeInputModel(
            "Pastel de nata",
            "A delicious Portuguese dessert",
            4,
            30,
            Cuisine.MEDITERRANEAN,
            MealType.DESSERT,
            emptyList(),
            emptyList(),
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

        val recipeId = jdbiCreateRecipe(
            JdbiRecipeModel(
                recipe.name,
                user.id,
                Date.from(Instant.now()),
                recipe.servings,
                recipe.preparationTime,
                recipe.cuisine,
                recipe.mealType,
                recipe.intolerances.map { it.ordinal },
                recipe.diets.map { it.ordinal },
                recipe.ingredients,
                picturesNames = listOf(pictureName)
            )
        )

        firestoreCreateRecipe(FirestoreRecipeModel(recipeId, recipe.description, recipe.instructions))
    }
}
