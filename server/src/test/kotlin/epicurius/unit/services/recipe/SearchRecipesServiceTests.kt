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
        intolerances = emptyList(),
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
        intolerances = emptyList(),
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
    fun `Should search for a recipe by name`() {
        // given a user id (USER_ID) and a search form with only recipe name filled and paging params
        val recipeName = searchRecipesInputInfoWithoutIngredients.name
        val recipeInput = SearchRecipesInputModel(recipeName)
        val pagingParams = PagingParams()

        // mock
        whenever(
            jdbiRecipeRepositoryMock.searchRecipes(
                USER_ID,
                recipeInput.toSearchRecipeModel(recipeName),
                pagingParams
            )
        ).thenReturn(listOf(recipeInfo))
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when searching for the recipe by name
        val results = searchRecipes(USER_ID, recipeInput, pagingParams.skip, pagingParams.limit)

        // then a list containing the recipe is returned successfully
        assertEquals(1, results.size)
        assertEquals(searchRecipesInputInfoWithoutIngredients.name, results.first().name)
        assertTrue(searchRecipesInputInfoWithoutIngredients.cuisine!!.contains(results.first().cuisine))
        assertTrue(searchRecipesInputInfoWithoutIngredients.mealType!!.contains(results.first().mealType))
        assertEquals(testPicture.bytes, results.first().picture)
    }

    @Test
    fun `Should search for a recipe according to user's intolerances`() {
        // given a user id (USER_ID) and a search form with recipe intolerance and paging params
        val recipeInput = SearchRecipesInputModel(intolerances = intoleranceList)
        val pagingParams = PagingParams()

        // mock
        whenever(
            jdbiRecipeRepositoryMock.searchRecipes(
                USER_ID,
                recipeInput.toSearchRecipeModel(recipeInput.name),
                pagingParams
            )
        ).thenReturn(emptyList())
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when searching for the recipe according to user's intolerance
        val results = searchRecipes(USER_ID, recipeInput, pagingParams.skip, pagingParams.limit)

        // then an empty list is returned
        assertTrue(results.isEmpty())

        // given a search form that do not match the recipe intolerance
        val recipeInputWithoutIntolerance = SearchRecipesInputModel(intolerances = listOf(Intolerance.WHEAT))

        // mock
        whenever(
            jdbiRecipeRepositoryMock.searchRecipes(
                USER_ID,
                recipeInputWithoutIntolerance.toSearchRecipeModel(recipeInputWithoutIntolerance.name),
                pagingParams
            )
        ).thenReturn(listOf(recipeInfo))
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when searching for the recipe according to user's intolerance
        val resultsWithoutIntolerance = searchRecipes(
            USER_ID,
            recipeInputWithoutIntolerance,
            pagingParams.skip,
            pagingParams.limit
        )

        // then a list containing the recipe is returned successfully
        assertEquals(1, resultsWithoutIntolerance.size)
        assertEquals(searchRecipesInputInfoWithoutIngredients.name, resultsWithoutIntolerance.first().name)
        assertTrue(searchRecipesInputInfoWithoutIngredients.cuisine!!.contains(resultsWithoutIntolerance.first().cuisine))
        assertTrue(searchRecipesInputInfoWithoutIngredients.mealType!!.contains(resultsWithoutIntolerance.first().mealType))
    }

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

    @Test
    fun `Should search for recipes of public users`() {
        // given an id of a public user (USER_ID) and a search form (searchRecipesInputInfoWithIngredients) and paging params
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

    @Test
    fun `Should search for recipes from private users when not followed`() {
        // given an id of a private user (AUTHOR_ID) and a search form (searchRecipesInputInfoWithIngredients) and paging params
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
        ).thenReturn(emptyList())
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when searching for the recipe with USER_ID, an user that does not follow the private user, the recipe's author
        val results = searchRecipes(USER_ID, searchRecipesInputInfoWithIngredients, pagingParams.skip, pagingParams.limit)

        // then an empty list is returned
        assertTrue(results.isEmpty())
    }

    @Test
    fun `Should search for recipes from private users when followed`() {
        // given an id of a private user (AUTHOR_ID) and a search form (searchRecipesInputInfoWithIngredients) and paging params
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

        // when searching for the recipe with USER_ID, an user that follows the private user, the recipe's author
        val results = searchRecipes(USER_ID, searchRecipesInputInfoWithIngredients, pagingParams.skip, pagingParams.limit)

        // then a list containing the recipe is returned successfully
        assertEquals(1, results.size)
        assertEquals(searchRecipesInputInfoWithIngredients.name, results.first().name)
        assertTrue(searchRecipesInputInfoWithIngredients.cuisine!!.contains(results.first().cuisine))
        assertTrue(searchRecipesInputInfoWithIngredients.mealType!!.contains(results.first().mealType))
        assertEquals(testPicture.bytes, results.first().picture)
    }
}
