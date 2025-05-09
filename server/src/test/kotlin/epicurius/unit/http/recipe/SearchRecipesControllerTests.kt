package epicurius.unit.http.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo
import epicurius.http.controllers.recipe.models.input.SearchRecipesInputModel
import epicurius.http.controllers.recipe.models.output.SearchRecipesOutputModel
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchRecipesControllerTests : RecipeHttpTest() {

    private val searchRecipesInputInfo = SearchRecipesInputModel(
        name = "Pastel de nata",
        cuisine = listOf(Cuisine.MEDITERRANEAN),
        mealType = listOf(MealType.DESSERT),
        ingredients = listOf("egg", "flour"),
        intolerances = listOf(Intolerance.EGG, Intolerance.GLUTEN),
        diets = listOf(Diet.OVO_VEGETARIAN, Diet.LACTO_VEGETARIAN),
        minCalories = 200,
        maxCalories = 500,
        minCarbs = 20,
        maxCarbs = 50,
        minFat = 10,
        maxFat = 30,
        minProtein = 5,
        maxProtein = 15,
        minTime = 20,
        maxTime = 60
    )

    private val recipeInfo = RecipeInfo(
        RECIPE_ID,
        testRecipe.name,
        testRecipe.cuisine,
        testRecipe.mealType,
        testRecipe.preparationTime,
        testRecipe.servings,
        testRecipe.pictures.first()
    )

    private val pagingParams = PagingParams()

    @Test
    fun `Should search for recipes by name`() {
        // given a search form with name and paging params (pagingParams)
        val searchRecipesInputInfoWithName = SearchRecipesInputModel(name = "Pastel")

        // mock
        whenever(
            recipeServiceMock
                .searchRecipes(testAuthenticatedUser.user.id, searchRecipesInputInfoWithName, pagingParams)
        ).thenReturn(listOf(recipeInfo))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when searching for recipes by name
        val response = searchRecipes(testAuthenticatedUser, searchRecipesInputInfoWithName.name, response =  mockResponse)
        val body = response.body as SearchRecipesOutputModel

        // then a list containing the recipe should is returned successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, body.recipes.size)
        assertEquals(recipeInfo, body.recipes.first())
    }

    @Test
    fun `Should search for a recipe according to user's intolerances`() {
        // given a search form with intolerances that match the recipe and paging params (pagingParams)
        val sameIntolerances = SearchRecipesInputModel(intolerances = listOf(Intolerance.GLUTEN))
        val pagingParams = PagingParams()

        // mock
        whenever(
            recipeServiceMock
                .searchRecipes(testAuthenticatedUser.user.id, sameIntolerances, pagingParams)
        ).thenReturn(emptyList())
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when searching for recipes with intolerances
        val response = searchRecipes(
            authenticatedUser = testAuthenticatedUser,
            intolerances = sameIntolerances.intolerances,
            response = mockResponse
        )
        val body = response.body as SearchRecipesOutputModel

        // then a list containing the recipe should is returned successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(body.recipes.isEmpty())

        // given a search form with intolerances that do not match the recipe and paging params
        val differentIntolerances = SearchRecipesInputModel(intolerances = listOf(Intolerance.DAIRY))

        // mock
        whenever(
            recipeServiceMock
                .searchRecipes(testAuthenticatedUser.user.id, differentIntolerances, pagingParams)
        ).thenReturn(listOf(recipeInfo))

        // when searching for recipes with intolerances
        val response2 = searchRecipes(
            authenticatedUser = testAuthenticatedUser,
            intolerances = differentIntolerances.intolerances,
            response = mockResponse
        )
        val body2 = response2.body as SearchRecipesOutputModel

        // then a list containing the recipe should is returned successfully
        assertEquals(HttpStatus.OK, response2.statusCode)
        assertEquals(1, body2.recipes.size)
        assertEquals(recipeInfo, body2.recipes.first())
    }

    @Test
    fun `Should search for a recipe without ingredients successfully`() {
        // given a search form without ingredients and paging params (pagingParams)
        val searchRecipesInputInfoWithoutIngredients = searchRecipesInputInfo.copy(
            ingredients = null
        )
        val pagingParams = PagingParams()

        // mock
        whenever(
            recipeServiceMock
                .searchRecipes(testAuthenticatedUser.user.id, searchRecipesInputInfoWithoutIngredients, pagingParams)
        ).thenReturn(listOf(recipeInfo))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when searching for recipes without ingredients
        val response = searchRecipes(
            testAuthenticatedUser,
            searchRecipesInputInfoWithoutIngredients.name,
            searchRecipesInputInfoWithoutIngredients.cuisine,
            searchRecipesInputInfoWithoutIngredients.mealType,
            searchRecipesInputInfoWithoutIngredients.ingredients,
            searchRecipesInputInfoWithoutIngredients.intolerances,
            searchRecipesInputInfoWithoutIngredients.diets,
            searchRecipesInputInfoWithoutIngredients.minCalories,
            searchRecipesInputInfoWithoutIngredients.maxCalories,
            searchRecipesInputInfoWithoutIngredients.minCarbs,
            searchRecipesInputInfoWithoutIngredients.maxCarbs,
            searchRecipesInputInfoWithoutIngredients.minFat,
            searchRecipesInputInfoWithoutIngredients.maxFat,
            searchRecipesInputInfoWithoutIngredients.minProtein,
            searchRecipesInputInfoWithoutIngredients.maxProtein,
            searchRecipesInputInfoWithoutIngredients.minTime,
            searchRecipesInputInfoWithoutIngredients.maxTime,
            mockResponse
        )
        val body = response.body as SearchRecipesOutputModel

        // then a list containing the recipe should is returned successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(recipeInfo, body.recipes.first())
    }

    @Test
    fun `Should search for a recipe with ingredients successfully`() {
        // given a search form with ingredients (searchRecipesInputInfo) and paging params (pagingParams)
        val pagingParams = PagingParams()

        // mock
        whenever(
            recipeServiceMock
                .searchRecipes(testAuthenticatedUser.user.id, searchRecipesInputInfo, pagingParams)
        ).thenReturn(listOf(recipeInfo))

        // when searching for recipes with ingredients
        val response = searchRecipes(
            testAuthenticatedUser,
            searchRecipesInputInfo.name,
            searchRecipesInputInfo.cuisine,
            searchRecipesInputInfo.mealType,
            searchRecipesInputInfo.ingredients,
            searchRecipesInputInfo.intolerances,
            searchRecipesInputInfo.diets,
            searchRecipesInputInfo.minCalories,
            searchRecipesInputInfo.maxCalories,
            searchRecipesInputInfo.minCarbs,
            searchRecipesInputInfo.maxCarbs,
            searchRecipesInputInfo.minFat,
            searchRecipesInputInfo.maxFat,
            searchRecipesInputInfo.minProtein,
            searchRecipesInputInfo.maxProtein,
            searchRecipesInputInfo.minTime,
            searchRecipesInputInfo.maxTime,
            mockResponse
        )
        val body = response.body as SearchRecipesOutputModel

        // then a list containing the recipe should is returned successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(recipeInfo, body.recipes.first())
    }

    @Test
    fun `Should search for recipes of public users`() {
        // given a search form with name and paging params (pagingParams)
        val pagingParams = PagingParams()

        // mock
        whenever(
            recipeServiceMock
                .searchRecipes(testAuthenticatedUser.user.id, searchRecipesInputInfo, pagingParams)
        ).thenReturn(listOf(recipeInfo))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when searching for public recipes
        val response = searchRecipes(
            testAuthenticatedUser,
            searchRecipesInputInfo.name,
            searchRecipesInputInfo.cuisine,
            searchRecipesInputInfo.mealType,
            searchRecipesInputInfo.ingredients,
            searchRecipesInputInfo.intolerances,
            searchRecipesInputInfo.diets,
            searchRecipesInputInfo.minCalories,
            searchRecipesInputInfo.maxCalories,
            searchRecipesInputInfo.minCarbs,
            searchRecipesInputInfo.maxCarbs,
            searchRecipesInputInfo.minFat,
            searchRecipesInputInfo.maxFat,
            searchRecipesInputInfo.minProtein,
            searchRecipesInputInfo.maxProtein,
            searchRecipesInputInfo.minTime,
            searchRecipesInputInfo.maxTime,
            mockResponse
        )
        val body = response.body as SearchRecipesOutputModel

        // then a list containing the recipe should is returned successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, body.recipes.size)
        assertEquals(recipeInfo, body.recipes.first())
    }

    @Test
    fun `Should search for recipes from private users when not followed`() {
        // given a search form with name and paging params (pagingParams)
        val pagingParams = PagingParams()

        // mock
        whenever(
            recipeServiceMock
                .searchRecipes(testAuthenticatedUser.user.id, searchRecipesInputInfo, pagingParams)
        ).thenReturn(emptyList())
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when searching for recipes from private users
        val response = searchRecipes(
            testAuthenticatedUser,
            searchRecipesInputInfo.name,
            searchRecipesInputInfo.cuisine,
            searchRecipesInputInfo.mealType,
            searchRecipesInputInfo.ingredients,
            searchRecipesInputInfo.intolerances,
            searchRecipesInputInfo.diets,
            searchRecipesInputInfo.minCalories,
            searchRecipesInputInfo.maxCalories,
            searchRecipesInputInfo.minCarbs,
            searchRecipesInputInfo.maxCarbs,
            searchRecipesInputInfo.minFat,
            searchRecipesInputInfo.maxFat,
            searchRecipesInputInfo.minProtein,
            searchRecipesInputInfo.maxProtein,
            searchRecipesInputInfo.minTime,
            searchRecipesInputInfo.maxTime,
            mockResponse
        )
        val body = response.body as SearchRecipesOutputModel

        // then an empty list should be returned,
        // testAuthenticatedUser does not follow the private user, recipe's author
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(0, body.recipes.size)
    }

    @Test
    fun `Should search for recipes from private users when followed`() {
        // given a search form with name and paging params (pagingParams)
        val pagingParams = PagingParams()

        // mock
        whenever(
            recipeServiceMock
                .searchRecipes(testAuthenticatedUser.user.id, searchRecipesInputInfo, pagingParams)
        ).thenReturn(listOf(recipeInfo))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when searching for recipes from private users
        val response = searchRecipes(
            testAuthenticatedUser,
            searchRecipesInputInfo.name,
            searchRecipesInputInfo.cuisine,
            searchRecipesInputInfo.mealType,
            searchRecipesInputInfo.ingredients,
            searchRecipesInputInfo.intolerances,
            searchRecipesInputInfo.diets,
            searchRecipesInputInfo.minCalories,
            searchRecipesInputInfo.maxCalories,
            searchRecipesInputInfo.minCarbs,
            searchRecipesInputInfo.maxCarbs,
            searchRecipesInputInfo.minFat,
            searchRecipesInputInfo.maxFat,
            searchRecipesInputInfo.minProtein,
            searchRecipesInputInfo.maxProtein,
            searchRecipesInputInfo.minTime,
            searchRecipesInputInfo.maxTime,
            mockResponse
        )
        val body = response.body as SearchRecipesOutputModel

        // then a list containing the recipe should is returned successfully
        // testAuthenticatedUser follows the private user, recipe's author
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, body.recipes.size)
        assertEquals(recipeInfo, body.recipes.first())
    }
}
