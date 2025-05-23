package epicurius.unit.services.recipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetRecipeServiceTests : RecipeServiceTest() {

    @Test
    fun `Should the author retrieve the recipe successfully`() {
        // given a recipe id (RECIPE_ID)

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(authorUsername, AUTHOR_ID)).thenReturn(true)
        whenever(runBlocking { firestoreRecipeRepositoryMock.getRecipeById(RECIPE_ID) }).thenReturn(firestoreRecipeInfo)
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when retrieving the recipe
        val recipe = runBlocking { getRecipe(RECIPE_ID, AUTHOR_ID) }

        // then the recipe is retrieved successfully
        assertEquals(RECIPE_ID, recipe.id)
        assertEquals(createRecipeInputInfo.name, recipe.name)
        assertEquals(authorUsername, recipe.authorUsername)
        assertEquals(createRecipeInputInfo.description, recipe.description)
        assertEquals(createRecipeInputInfo.servings, recipe.servings)
        assertEquals(createRecipeInputInfo.preparationTime, recipe.preparationTime)
        assertEquals(createRecipeInputInfo.cuisine, recipe.cuisine)
        assertEquals(createRecipeInputInfo.mealType, recipe.mealType)
        assertEquals(createRecipeInputInfo.intolerances.toList(), recipe.intolerances)
        assertEquals(createRecipeInputInfo.diets.toList(), recipe.diets)
        assertEquals(createRecipeInputInfo.ingredients, recipe.ingredients)
        assertEquals(createRecipeInputInfo.calories, recipe.calories)
        assertEquals(createRecipeInputInfo.protein, recipe.protein)
        assertEquals(createRecipeInputInfo.fat, recipe.fat)
        assertEquals(createRecipeInputInfo.carbs, recipe.carbs)
        assertEquals(createRecipeInputInfo.instructions, recipe.instructions)
        assertContentEquals(recipePictures.map { it.bytes }, recipe.pictures)
    }

    @Test
    fun `Should a follower of the author retrieve the recipe successfully`() {
        // given a recipe id (RECIPE_ID) and a user following the author
        val userId = 1904

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(authorUsername, userId)).thenReturn(true)
        whenever(runBlocking { firestoreRecipeRepositoryMock.getRecipeById(RECIPE_ID) }).thenReturn(firestoreRecipeInfo)
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when retrieving the recipe
        val recipe = runBlocking { getRecipe(RECIPE_ID, userId) }

        // then the recipe is retrieved successfully
        assertEquals(RECIPE_ID, recipe.id)
        assertEquals(createRecipeInputInfo.name, recipe.name)
        assertEquals(authorUsername, recipe.authorUsername)
        assertEquals(createRecipeInputInfo.description, recipe.description)
        assertEquals(createRecipeInputInfo.servings, recipe.servings)
        assertEquals(createRecipeInputInfo.preparationTime, recipe.preparationTime)
        assertEquals(createRecipeInputInfo.cuisine, recipe.cuisine)
        assertEquals(createRecipeInputInfo.mealType, recipe.mealType)
        assertEquals(createRecipeInputInfo.intolerances.toList(), recipe.intolerances)
        assertEquals(createRecipeInputInfo.diets.toList(), recipe.diets)
        assertEquals(createRecipeInputInfo.ingredients, recipe.ingredients)
        assertEquals(createRecipeInputInfo.calories, recipe.calories)
        assertEquals(createRecipeInputInfo.protein, recipe.protein)
        assertEquals(createRecipeInputInfo.fat, recipe.fat)
        assertEquals(createRecipeInputInfo.carbs, recipe.carbs)
        assertEquals(createRecipeInputInfo.instructions, recipe.instructions)
        assertContentEquals(recipePictures.map { it.bytes }, recipe.pictures)
    }

    @Test
    fun `Should throw RecipeNotFound exception when retrieving a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(nonExistingRecipeId)).thenReturn(null, jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(authorUsername, AUTHOR_ID)).thenReturn(true)

        // when retrieving the recipe
        // then the recipe is not retrieved and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> { runBlocking { getRecipe(nonExistingRecipeId, AUTHOR_ID) } } // jdbi
        assertFailsWith<RecipeNotFound> { runBlocking { getRecipe(nonExistingRecipeId, AUTHOR_ID) } } // firestore
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when retrieving a recipe from a private user not followed`() {
        // given a user not following the author and a recipe id (RECIPE_ID)
        val userId = 123

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(authorUsername, userId)).thenReturn(false)

        // when retrieving the recipe
        // then the recipe is not retrieved and throws RecipeNotAccessible exception
        assertFailsWith<RecipeNotAccessible> { runBlocking { getRecipe(RECIPE_ID, userId) } }
    }
}
