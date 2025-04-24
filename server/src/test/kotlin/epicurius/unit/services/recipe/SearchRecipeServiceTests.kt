package epicurius.unit.services.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class SearchRecipeServiceTests : RecipeServiceTest() {

    private val searchRecipesInfoWithoutIngredients = SearchRecipesInputModel(
        name = "Pastel de nata",
        cuisine = Cuisine.MEDITERRANEAN,
        mealType = MealType.DESSERT,
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

    private val searchRecipesInfoWithIngredients = SearchRecipesInputModel(
        name = "Pastel de nata",
        cuisine = Cuisine.MEDITERRANEAN,
        mealType = MealType.DESSERT,
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
        // given a user id  (USER_ID) and a search form
        val searchRecipesInputModel = searchRecipesInfoWithoutIngredients
        val jdbiRecipeInfo = recipeInfo

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
        val results = searchRecipes(USER_ID, searchRecipesInputModel)

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
        val searchRecipesInputModel = searchRecipesInfoWithIngredients
        val jdbiRecipeInfo = recipeInfo

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
        val results = searchRecipes(USER_ID, searchRecipesInputModel)

        // then a list containing the recipe is returned
        assertEquals(1, results.size)
        assertEquals(searchRecipesInputModel.name, results.first().name)
        assertEquals(searchRecipesInputModel.cuisine, results.first().cuisine)
        assertEquals(searchRecipesInputModel.mealType, results.first().mealType)
        assertEquals(testPicture.bytes, results.first().picture)
    }
}
