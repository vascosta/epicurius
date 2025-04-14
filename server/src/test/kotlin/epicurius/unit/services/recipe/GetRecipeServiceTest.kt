package epicurius.unit.services.recipe

import epicurius.domain.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.exceptions.RecipeNotFound
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetRecipeServiceTest: RecipeServiceTest() {

    @Test
    fun `Should retrieve the recipe successfully`() {
        // given a recipe id
        // mocks
        val jdbiRecipeModel = getJdbiRecipeModel()
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(runBlocking { firestoreRecipeRepositoryMock.getRecipe(RECIPE_ID) } ).thenReturn(firestoreRecipeModel)
        whenever(cloudStoragePictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when retrieving the recipe
        val recipe = runBlocking { recipeService.getRecipe(RECIPE_ID) }

        // then the recipe is retrieved successfully
        assertEquals(RECIPE_ID, recipe.id)
        assertEquals(recipeInfo.name, recipe.name)
        assertEquals(authorName, recipe.authorUsername)
        assertEquals(recipeInfo.description, recipe.description)
        assertEquals(recipeInfo.servings, recipe.servings)
        assertEquals(recipeInfo.preparationTime, recipe.preparationTime)
        assertEquals(recipeInfo.cuisine, recipe.cuisine)
        assertEquals(recipeInfo.mealType, recipe.mealType)
        assertEquals(recipeInfo.intolerances, recipe.intolerances)
        assertEquals(recipeInfo.diets, recipe.diets)
        assertEquals(recipeInfo.ingredients, recipe.ingredients)
        assertEquals(recipeInfo.calories, recipe.calories)
        assertEquals(recipeInfo.protein, recipe.protein)
        assertEquals(recipeInfo.fat, recipe.fat)
        assertEquals(recipeInfo.carbs, recipe.carbs)
        assertEquals(recipeInfo.instructions, recipe.instructions)
        assertContentEquals(recipePictures.map { it.bytes }, recipe.pictures)
    }

    @Test
    fun `Should throw RecipeNotFound exception when retrieving a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        val jdbiRecipeModel = getJdbiRecipeModel()
        whenever(jdbiRecipeRepositoryMock.getRecipe(nonExistingRecipeId))
            .thenReturn(null)
            .thenReturn(jdbiRecipeModel)

        // when retrieving the recipe
        val exception = assertFailsWith<RecipeNotFound> {
            runBlocking { recipeService.getRecipe(nonExistingRecipeId) }
        }

        val exception2 = assertFailsWith<RecipeNotFound> {
            runBlocking { recipeService.getRecipe(nonExistingRecipeId) }
        }

        // then an exception is thrown
        assertEquals(RecipeNotFound().message, exception.message)
        assertEquals(RecipeNotFound().message, exception2.message)
    }
}