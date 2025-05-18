package epicurius.unit.repository.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
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
            picturesNames = listOf("")
        )

        // when creating the recipe
        val recipeId = jdbiCreateRecipe(jdbiCreateRecipeInfo)

        // then the recipe is created successfully
        val jdbiRecipe = getJdbiRecipeById(recipeId)
        assertNotNull(jdbiRecipe)
        assertEquals(jdbiCreateRecipeInfo.name, jdbiRecipe.name)
        assertEquals(jdbiCreateRecipeInfo.authorId, jdbiRecipe.authorId)
        assertEquals(testAuthor.user.name, jdbiRecipe.authorUsername)
        assertEquals(jdbiCreateRecipeInfo.servings, jdbiRecipe.servings)
        assertEquals(jdbiCreateRecipeInfo.preparationTime, jdbiRecipe.preparationTime)
        assertEquals(jdbiCreateRecipeInfo.cuisine, jdbiRecipe.cuisine.ordinal)
        assertEquals(jdbiCreateRecipeInfo.mealType, jdbiRecipe.mealType.ordinal)
        assertEquals(jdbiCreateRecipeInfo.intolerances, jdbiRecipe.intolerances.map { it.ordinal })
        assertEquals(jdbiCreateRecipeInfo.diets, jdbiRecipe.diets.map { it.ordinal })
        assertEquals(jdbiCreateRecipeInfo.ingredients, jdbiRecipe.ingredients)
        assertEquals(jdbiCreateRecipeInfo.calories, jdbiRecipe.calories)
        assertEquals(jdbiCreateRecipeInfo.protein, jdbiRecipe.protein)
        assertEquals(jdbiCreateRecipeInfo.fat, jdbiRecipe.fat)
        assertEquals(jdbiCreateRecipeInfo.carbs, jdbiRecipe.carbs)
        assertEquals(jdbiCreateRecipeInfo.picturesNames, jdbiRecipe.picturesNames)

        // when creating the recipe in Firestore
        val firestoreRecipeInfo = FirestoreRecipeModel(
            recipeId,
            "A delicious Portuguese dessert",
            Instructions(
                mapOf(
                    "1" to "Preheat the oven to 200°C.",
                    "2" to "In a bowl, mix the eggs, sugar, flour, and milk.",
                    "3" to "Pour the mixture into pastry shells.",
                    "4" to "Bake for 20 minutes or until golden brown.",
                    "5" to "Let cool before serving."
                )
            )
        )

        firestoreCreateRecipe(firestoreRecipeInfo)

        // then the recipe is created successfully
        val firestoreRecipe = runBlocking { getFirestoreRecipeById(recipeId) }
        assertNotNull(firestoreRecipe)
        assertEquals(firestoreRecipeInfo.description, firestoreRecipe.description)
        assertEquals(firestoreRecipeInfo.instructions, firestoreRecipe.instructions)
    }

    @Test
    fun `Should create a recipe and then delete it successfully`() {
        // given a recipe
        val recipe = createTestRecipe(tm, fs, testAuthor.user)

        // when deleting the recipe
        deleteJdbiRecipe(recipe.id)
        deleteFirestoreRecipe(recipe.id)

        // then the recipe is deleted successfully
        val jdbiRecipe = getJdbiRecipeById(recipe.id)
        val firestoreRecipe = runBlocking { getFirestoreRecipeById(recipe.id) }
        assertNull(jdbiRecipe)
        assertNull(firestoreRecipe)
    }
}
