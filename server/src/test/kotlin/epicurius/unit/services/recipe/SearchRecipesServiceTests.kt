package epicurius.unit.services.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchRecipesServiceTests : RecipeServiceTest() {

    private val searchRecipesInputInfoWithoutIngredients = SearchRecipesInputModel(
        name = "Pastel de nata",
        cuisine = listOf(Cuisine.MEDITERRANEAN),
        mealType = listOf(MealType.DESSERT),
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

    private val searchRecipesInputInfoWithIngredients = SearchRecipesInputModel(
        name = "Pastel de nata",
        cuisine = listOf(Cuisine.MEDITERRANEAN),
        mealType = listOf(MealType.DESSERT),
        ingredients = listOf("Eggs", "Sugar"),
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

    private val recipeInfo = JdbiRecipeInfo(
        id = RECIPE_ID,
        name = "Pastel de nata",
        cuisine = Cuisine.MEDITERRANEAN,
        mealType = MealType.DESSERT,
        preparationTime = 30,
        servings = 4,
        pictures = recipePicturesNames
    )

    @Test
    fun `Should search for a recipe without ingredients successfully`() {
        // given a user id (USER_ID) and a search form (searchRecipesInputInfoWithoutIngredients) and paging params
        val pagingParams = PagingParams()

        // mock
        whenever(
            jdbiRecipeRepositoryMock.searchRecipes(
                USER_ID,
                searchRecipesInputInfoWithoutIngredients.toSearchRecipeModel(
                    searchRecipesInputInfoWithoutIngredients.name
                ),
                pagingParams
            )
        ).thenReturn(listOf(recipeInfo))
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when searching for the recipe
        val results = searchRecipes(USER_ID, searchRecipesInputInfoWithoutIngredients, pagingParams.skip, pagingParams.limit)

        // then a list containing the recipe is returned successfully
        assertEquals(1, results.size)
        assertEquals(searchRecipesInputInfoWithoutIngredients.name, results.first().name)
        assertTrue(searchRecipesInputInfoWithoutIngredients.cuisine!!.contains(results.first().cuisine))
        assertTrue(searchRecipesInputInfoWithoutIngredients.mealType!!.contains(results.first().mealType))
        assertEquals(testPicture.bytes, results.first().picture)
    }

    @Test
    fun `Should search for a recipe with ingredients successfully`() {
        // given a user id (USER_ID) and a search form (searchRecipesInputInfoWithIngredients) and paging params
        val pagingParams = PagingParams()

        // mock
        whenever(
            jdbiRecipeRepositoryMock.searchRecipes(
                USER_ID,
                searchRecipesInputInfoWithIngredients.toSearchRecipeModel(
                    searchRecipesInputInfoWithIngredients.name
                ),
                pagingParams
            )
        ).thenReturn(listOf(recipeInfo))
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when searching for the recipe
        val results = searchRecipes(USER_ID, searchRecipesInputInfoWithIngredients, pagingParams.skip, pagingParams.limit)

        // then a list containing the recipe is returned successfully
        assertEquals(1, results.size)
        assertEquals(searchRecipesInputInfoWithIngredients.name, results.first().name)
        assertTrue(searchRecipesInputInfoWithIngredients.cuisine!!.contains(results.first().cuisine))
        assertTrue(searchRecipesInputInfoWithIngredients.mealType!!.contains(results.first().mealType))
        assertEquals(testPicture.bytes, results.first().picture)
    }
}
