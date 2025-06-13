package epicurius.unit.repository.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import epicurius.utils.createTestRecipe
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CreateRecipeRepositoryTests : RecipeRepositoryTest() {

    @Test
    fun `Should create a recipe and then retrieve it successfully`() {
        // given information for a new recipe
        val jdbiCreateRecipeInfo = JdbiCreateRecipeModel(
            name = "Pastel de nata",
            authorId = testAuthor.user.id,
            description = "Pastel de nata",
            servings = 4,
            preparationTime = 30,
            cuisine = Cuisine.MEDITERRANEAN.ordinal,
            mealType = MealType.DESSERT.ordinal,
            intolerances = listOf(Intolerance.EGG, Intolerance.GLUTEN, Intolerance.DAIRY).map { it.ordinal },
            diets = listOf(Diet.OVO_VEGETARIAN, Diet.LACTO_VEGETARIAN).map { it.ordinal },
            ingredients = listOf(
                Ingredient("Eggs", 4.0, IngredientUnit.X),
                Ingredient("Sugar", 200.0, IngredientUnit.G),
                Ingredient("Flour", 100.0, IngredientUnit.G),
                Ingredient("Milk", 500.0, IngredientUnit.ML),
                Ingredient("Butter", 50.0, IngredientUnit.G)
            ),
            instructions = Instructions(
                mapOf(
                    "1" to "Preheat the oven to 200°C.",
                    "2" to "In a bowl, mix the eggs, sugar, flour, and milk.",
                    "3" to "Pour the mixture into pastry shells.",
                    "4" to "Bake for 20 minutes or until golden brown.",
                    "5" to "Let cool before serving."
                )
            ),
            picturesNames = listOf("")
        )

        // when creating the recipe
        val recipeId = jdbiCreateRecipe(jdbiCreateRecipeInfo)

        // then the recipe is created successfully
        val jdbiRecipeById = getJdbiRecipeById(recipeId)
        assertNotNull(jdbiRecipeById)
        assertEquals(jdbiCreateRecipeInfo.name, jdbiRecipeById.name)
        assertEquals(jdbiCreateRecipeInfo.authorId, jdbiRecipeById.authorId)
        assertEquals(testAuthor.user.name, jdbiRecipeById.authorUsername)
        assertEquals(jdbiCreateRecipeInfo.description, jdbiRecipeById.description
        assertEquals(jdbiCreateRecipeInfo.servings, jdbiRecipeById.servings)
        assertEquals(jdbiCreateRecipeInfo.preparationTime, jdbiRecipeById.preparationTime)
        assertEquals(jdbiCreateRecipeInfo.cuisine, jdbiRecipeById.cuisine.ordinal)
        assertEquals(jdbiCreateRecipeInfo.mealType, jdbiRecipeById.mealType.ordinal)
        assertEquals(jdbiCreateRecipeInfo.intolerances, jdbiRecipeById.intolerances.map { it.ordinal })
        assertEquals(jdbiCreateRecipeInfo.diets, jdbiRecipeById.diets.map { it.ordinal })
        assertEquals(jdbiCreateRecipeInfo.ingredients, jdbiRecipeById.ingredients)
        assertEquals(jdbiCreateRecipeInfo.calories, jdbiRecipeById.calories)
        assertEquals(jdbiCreateRecipeInfo.protein, jdbiRecipeById.protein)
        assertEquals(jdbiCreateRecipeInfo.fat, jdbiRecipeById.fat)
        assertEquals(jdbiCreateRecipeInfo.carbs, jdbiRecipeById.carbs)
        assertEquals(jdbiCreateRecipeInfo.instructions, jdbiRecipeById.instructions)
        assertEquals(jdbiCreateRecipeInfo.picturesNames, jdbiRecipeById.picturesNames)
    }

    @Test
    fun `Should create a recipe and then delete it successfully`() {
        // given a recipe
        val recipe = createTestRecipe(tm, testAuthor.user)

        // when deleting the recipe
        deleteJdbiRecipe(recipe.id)

        // then the recipe is deleted successfully
        val jdbiRecipe = getJdbiRecipeById(recipe.id)
        assertNull(jdbiRecipe)
    }
}
