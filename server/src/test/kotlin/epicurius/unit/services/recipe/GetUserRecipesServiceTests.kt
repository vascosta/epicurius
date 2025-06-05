package epicurius.unit.services.recipe

import epicurius.domain.PagingParams
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertTrue

class GetUserRecipesServiceTests: RecipeServiceTest() {

    @Test
    fun `Should retrieve the user's recipes successfully`() {
        // given a user id (AUTHOR_ID)
        val pagingParams = PagingParams()

        // mock
        val jdbiRecipeInfoMock = JdbiRecipeInfo(
            id = RECIPE_ID,
            name = createRecipeInputInfo.name,
            authorUsername = authorUsername,
            rating = 0.0,
            cuisine = createRecipeInputInfo.cuisine,
            mealType = createRecipeInputInfo.mealType,
            preparationTime = createRecipeInputInfo.preparationTime,
            servings = createRecipeInputInfo.servings,
            picturesNames = listOf(testPicture.name),
        )
        whenever(jdbiRecipeRepositoryMock.getUserRecipes(AUTHOR_ID, pagingParams))
            .thenReturn(listOf(jdbiRecipeInfoMock))
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when retrieving the user's recipes
        val recipes = runBlocking { getUserRecipes(AUTHOR_ID, pagingParams) }

        // then the recipes are retrieved successfully
        assertTrue(recipes.contains(jdbiRecipeInfoMock.toRecipeInfo(testPicture.bytes)))
    }
}