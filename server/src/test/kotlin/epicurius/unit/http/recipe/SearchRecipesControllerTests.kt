package epicurius.unit.http.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.http.recipe.models.output.SearchRecipesOutputModel
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class SearchRecipesControllerTests : RecipeHttpTest() {

    val searchRecipesInputInfo = SearchRecipesInputModel(
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

    val recipeInfo = RecipeInfo(
        RECIPE_ID,
        testRecipe.name,
        testRecipe.cuisine,
        testRecipe.mealType,
        testRecipe.preparationTime,
        testRecipe.servings,
        testRecipe.pictures.first()
    )

    @Test
    fun `Should search for a recipe without ingredients successfully`() {
        // given a search form without ingredients and paging params
        val searchRecipesInputInfoWithoutIngredients = searchRecipesInputInfo.copy(
            ingredients = null
        )
        val pagingParams = PagingParams()

        // mock
        whenever(
            recipeServiceMock
                .searchRecipes(testAuthenticatedUser.user.id, searchRecipesInputInfoWithoutIngredients, pagingParams)
        ).thenReturn(listOf(recipeInfo))

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
            searchRecipesInputInfoWithoutIngredients.maxTime
        )
        val body = response.body as SearchRecipesOutputModel

        // then a list containing the recipe should is returned successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(recipeInfo, body.recipes.first())
    }

    @Test
    fun `Should search for a recipe with ingredients successfully`() {
        // given a search form with ingredients (searchRecipesInputInfo) and paging params
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
            searchRecipesInputInfo.maxTime
        )
        val body = response.body as SearchRecipesOutputModel

        // then a list containing the recipe should is returned successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(recipeInfo, body.recipes.first())
    }
}
