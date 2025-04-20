package epicurius.unit.services.recipe

import epicurius.domain.PictureDomain.Companion.RECIPES_FOLDER
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class SearchRecipeServiceTests : RecipeServiceTest() {

    @Test
    fun `Should search for a recipe without ingredients successfully`() {
        // given a user id  (USER_ID) and a search form
        val searchRecipesInputModel = getSearchRecipesWithoutIngredientsInputModel()
        val jdbiRecipeInfo = getRecipeInfo()

        // mock
        whenever(
            jdbiRecipeRepositoryMock.searchRecipes(
                USER_ID,
                searchRecipesInputModel.toSearchRecipe(
                    searchRecipesInputModel.name
                )
            )
        ).thenReturn(listOf(jdbiRecipeInfo))
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when searching for the recipe
        val results = recipeService.searchRecipes(USER_ID, searchRecipesInputModel)

        // then a list containing the recipe is returned
        assertEquals(1, results.size)
        assertEquals(searchRecipesInputModel.name, results.first().name)
        assertEquals(searchRecipesInputModel.cuisine, results.first().cuisine)
        assertEquals(searchRecipesInputModel.mealType, results.first().mealType)
        assertEquals(testPicture.bytes, results.first().picture)
    }

    @Test
    fun `Should search for a recipe with ingredients successfully`() {
        // given a user id (USER_ID) and a search form
        val searchRecipesInputModel = getSearchRecipesWithIngredientsInputModel()
        val jdbiRecipeInfo = getRecipeInfo()

        // mock
        whenever(
            jdbiRecipeRepositoryMock.searchRecipes(
                USER_ID,
                searchRecipesInputModel.toSearchRecipe(
                    searchRecipesInputModel.name
                )
            )
        ).thenReturn(listOf(jdbiRecipeInfo))
        whenever(
            searchRecipesInputModel.ingredients?.let {
                jdbiRecipeRepositoryMock.searchRecipesByIngredients(USER_ID, it)
            }
        ).thenReturn(listOf(jdbiRecipeInfo))
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when searching for the recipe
        val results = recipeService.searchRecipes(USER_ID, searchRecipesInputModel)

        // then a list containing the recipe is returned
        assertEquals(1, results.size)
        assertEquals(searchRecipesInputModel.name, results.first().name)
        assertEquals(searchRecipesInputModel.cuisine, results.first().cuisine)
        assertEquals(searchRecipesInputModel.mealType, results.first().mealType)
        assertEquals(testPicture.bytes, results.first().picture)
    }
}
