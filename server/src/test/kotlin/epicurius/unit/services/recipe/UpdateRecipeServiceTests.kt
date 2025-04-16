package epicurius.unit.services.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.NotTheAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateRecipeServiceTests: RecipeServiceTest() {

    @Test
    fun `Should update a recipe successfully`() {
        // given information to update a recipe (jdbiUpdateRecipeInfo, firestoreUpdateRecipeInfo)
        // mock
        val jdbiRecipeModel = JdbiRecipeModel(
            RECIPE_ID,
            "name",
            AUTHOR_ID,
            authorName,
            jdbiCreateRecipeInfo.date,
            1,
            1,
            Cuisine.ASIAN,
            MealType.SOUP,
            listOf(Intolerance.PEANUT),
            listOf(Diet.KETOGENIC),
            listOf(
                Ingredient("Ingredient1", 1, IngredientUnit.TSP),
                Ingredient("Ingredient2", 1, IngredientUnit.TSP)
            ),
            1,
            1,
            1,
            1,
            recipePicturesNames,
        )
        val firestoreRecipeModel = FirestoreRecipeModel(
            RECIPE_ID,
            "description",
            Instructions(mapOf("1" to "Step1", "2" to "Step2"))
        )
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(getJdbiRecipeModel(jdbiCreateRecipeInfo.date))
        whenever(jdbiRecipeRepositoryMock.updateRecipe(jdbiUpdateRecipeInfo)).thenReturn(jdbiRecipeModel)
        whenever(runBlocking { firestoreRecipeRepositoryMock.updateRecipe(firestoreUpdateRecipeInfo) }).thenReturn(firestoreRecipeModel)

        // when updating the recipe
        val updatedRecipe = runBlocking { recipeService.updateRecipe(AUTHOR_ID, RECIPE_ID, updateRecipeInfo) }

        // then the recipe is updated successfully
        assertEquals(RECIPE_ID, updatedRecipe.id)
        assertEquals(updateRecipeInfo.name, updatedRecipe.name)
        assertEquals(authorName, updatedRecipe.authorUsername)
        assertEquals(updateRecipeInfo.description, updatedRecipe.description)
        assertEquals(updateRecipeInfo.servings, updatedRecipe.servings)
        assertEquals(updateRecipeInfo.preparationTime, updatedRecipe.preparationTime)
        assertEquals(updateRecipeInfo.cuisine, updatedRecipe.cuisine)
        assertEquals(updateRecipeInfo.mealType, updatedRecipe.mealType)
        assertEquals(updateRecipeInfo.intolerances, updatedRecipe.intolerances)
        assertEquals(updateRecipeInfo.diets, updatedRecipe.diets)
        assertEquals(updateRecipeInfo.ingredients, updatedRecipe.ingredients)
        assertEquals(updateRecipeInfo.calories, updatedRecipe.calories)
        assertEquals(updateRecipeInfo.protein, updatedRecipe.protein)
        assertEquals(updateRecipeInfo.fat, updatedRecipe.fat)
        assertEquals(updateRecipeInfo.carbs, updatedRecipe.carbs)
        assertEquals(updateRecipeInfo.instructions, updatedRecipe.instructions)
    }

    @Test
    fun `Should throw RecipeNotFound exception when updating a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipe(nonExistingRecipeId)).thenReturn(null)

        // when updating the recipe
        val exception = assertFailsWith<RecipeNotFound> {
            runBlocking { recipeService.updateRecipe(AUTHOR_ID, nonExistingRecipeId, updateRecipeInfo) }
        }

        // then an exception is thrown
        assertEquals(RecipeNotFound().message, exception.message)
    }

    @Test
    fun `Should throw NotTheAuthor exception when updating a recipe that does not belong to the user`() {
        // given a recipe id that does not belong to the user (RECIPE_ID)
        val userId = 9999

        // mock
        val jdbiCreateRecipeModel = createRecipeInfo.toJdbiCreateRecipeModel(AUTHOR_ID, recipePicturesNames)
        val jdbiRecipeModel = getJdbiRecipeModel(jdbiCreateRecipeModel.date)
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(jdbiRecipeModel)

        // when updating the recipe
        val exception = assertFailsWith<NotTheAuthor> {
            runBlocking { recipeService.updateRecipe(userId, RECIPE_ID, updateRecipeInfo) }
        }

        // then an exception is thrown
        assertEquals(NotTheAuthor().message, exception.message)
    }
}
