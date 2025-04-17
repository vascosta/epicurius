package epicurius.unit.services.recipe

import epicurius.domain.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.exceptions.RecipeNotFound
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetRecipeServiceTests : RecipeServiceTest() {

    @Test
    fun `Should retrieve the recipe successfully`() {
        // given a recipe id (RECIPE_ID)

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(runBlocking { firestoreRecipeRepositoryMock.getRecipe(RECIPE_ID) }).thenReturn(firestoreRecipeInfo)
        whenever(cloudStoragePictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when retrieving the recipe
        val recipe = runBlocking { recipeService.getRecipe(RECIPE_ID) }

        // then the recipe is retrieved successfully
        assertEquals(RECIPE_ID, recipe.id)
        assertEquals(createRecipeInfo.name, recipe.name)
        assertEquals(authorName, recipe.authorUsername)
        assertEquals(createRecipeInfo.description, recipe.description)
        assertEquals(createRecipeInfo.servings, recipe.servings)
        assertEquals(createRecipeInfo.preparationTime, recipe.preparationTime)
        assertEquals(createRecipeInfo.cuisine, recipe.cuisine)
        assertEquals(createRecipeInfo.mealType, recipe.mealType)
        assertEquals(createRecipeInfo.intolerances, recipe.intolerances)
        assertEquals(createRecipeInfo.diets, recipe.diets)
        assertEquals(createRecipeInfo.ingredients, recipe.ingredients)
        assertEquals(createRecipeInfo.calories, recipe.calories)
        assertEquals(createRecipeInfo.protein, recipe.protein)
        assertEquals(createRecipeInfo.fat, recipe.fat)
        assertEquals(createRecipeInfo.carbs, recipe.carbs)
        assertEquals(createRecipeInfo.instructions, recipe.instructions)
        assertContentEquals(recipePictures.map { it.bytes }, recipe.pictures)
    }

    @Test
    fun `Should throw RecipeNotFound exception when retrieving a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipe(nonExistingRecipeId))
            .thenReturn(null)
            .thenReturn(jdbiRecipeModel)

        // when retrieving the recipe
        val exception = assertFailsWith<RecipeNotFound> {
            runBlocking { recipeService.getRecipe(nonExistingRecipeId) } // jdbi
        }

        val exception2 = assertFailsWith<RecipeNotFound> {
            runBlocking { recipeService.getRecipe(nonExistingRecipeId) } // firestore
        }

        // then an exception is thrown
        assertEquals(RecipeNotFound().message, exception.message)
        assertEquals(RecipeNotFound().message, exception2.message)
    }
}
