package epicurius.unit.repository.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.repository.firestore.recipe.models.FirestoreUpdateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel
import epicurius.utils.generateRandomRecipeDescription
import epicurius.utils.generateRandomRecipeIngredients
import epicurius.utils.generateRandomRecipeInstructions
import epicurius.utils.generateRandomRecipeName
import kotlinx.coroutines.runBlocking
import java.util.UUID.randomUUID
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateRecipeRepositoryTests : RecipeRepositoryTest() {

    @Test
    fun `Should update a recipe and then retrieve it successfully`() {
        // given a recipe (testRecipe) and new information for updating it
        val jdbiUpdateRecipeInfo = JdbiUpdateRecipeModel(
            testRecipe.id,
            generateRandomRecipeName(),
            1,
            1,
            Cuisine.ASIAN.ordinal,
            MealType.SOUP.ordinal,
            setOf(Intolerance.PEANUT.ordinal),
            setOf(Diet.KETOGENIC.ordinal),
            generateRandomRecipeIngredients(),
            1,
            9,
            0,
            4,
            listOf(randomUUID().toString())
        )

        // when updating the recipe
        val updatedJdbiRecipe = updateJdbiRecipe(jdbiUpdateRecipeInfo)

        // then the recipe is updated successfully
        assertEquals(jdbiUpdateRecipeInfo.name, updatedJdbiRecipe.name)
        assertEquals(testAuthor.user.name, updatedJdbiRecipe.authorUsername)
        assertEquals(jdbiUpdateRecipeInfo.servings, updatedJdbiRecipe.servings)
        assertEquals(jdbiUpdateRecipeInfo.preparationTime, updatedJdbiRecipe.preparationTime)
        assertEquals(jdbiUpdateRecipeInfo.cuisine, updatedJdbiRecipe.cuisine.ordinal)
        assertEquals(jdbiUpdateRecipeInfo.mealType, updatedJdbiRecipe.mealType.ordinal)
        assertEquals(jdbiUpdateRecipeInfo.intolerances, updatedJdbiRecipe.intolerances.map { it.ordinal }.toSet())
        assertEquals(jdbiUpdateRecipeInfo.diets, updatedJdbiRecipe.diets.map { it.ordinal }.toSet())
        assertEquals(jdbiUpdateRecipeInfo.ingredients, updatedJdbiRecipe.ingredients)
        assertEquals(jdbiUpdateRecipeInfo.calories, updatedJdbiRecipe.calories)
        assertEquals(jdbiUpdateRecipeInfo.protein, updatedJdbiRecipe.protein)
        assertEquals(jdbiUpdateRecipeInfo.fat, updatedJdbiRecipe.fat)
        assertEquals(jdbiUpdateRecipeInfo.carbs, updatedJdbiRecipe.carbs)
        assertEquals(jdbiUpdateRecipeInfo.picturesNames, updatedJdbiRecipe.picturesNames)

        // when updating the recipe in Firestore
        val firestoreRecipeInfo = FirestoreUpdateRecipeModel(
            testRecipe.id,
            generateRandomRecipeDescription(),
            generateRandomRecipeInstructions()
        )

        val updatedFirestoreRecipe = runBlocking { updateFirestoreRecipe(firestoreRecipeInfo) }

        // then the recipe is updated successfully
        assertEquals(firestoreRecipeInfo.description, updatedFirestoreRecipe.description)
        assertEquals(firestoreRecipeInfo.instructions, updatedFirestoreRecipe.instructions)
    }
}
