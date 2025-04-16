package epicurius.unit.services.recipe

import epicurius.domain.PictureDomain.Companion.RECIPES_FOLDER
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class SearchRecipeServiceTests: RecipeServiceTest() {

    @Test
    fun `Should search for a recipe without ingredients successfully`() {
        // given a user id and a search form
        val userId = 6798
        val searchRecipesInputModel = getSearchRecipesInputModel()
        // mock
        val jdbiRecipeInfo = getJdbiRecipeInfo()
        whenever(
            jdbiRecipeRepositoryMock.searchRecipes(userId, searchRecipesInputModel.toSearchRecipe(
                searchRecipesInputModel.name
            ))
        ).thenReturn(listOf(jdbiRecipeInfo))
        whenever(cloudStoragePictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when searching for the recipe
        val results = recipeService.searchRecipes(userId, searchRecipesInputModel)

        // then a list containing the recipe is returned
        assertEquals(1, results.size)
        assertEquals(searchRecipesInputModel.name, results.first().name)
        assertEquals(searchRecipesInputModel.cuisine, results.first().cuisine)
        assertEquals(searchRecipesInputModel.mealType, results.first().mealType)
        assertEquals(testPicture.bytes, results.first().picture)
    }

    /*
    val userId = 67890

        // given a search form
        val form = SearchRecipesModel(
            name = "Burrito",
            cuisine = Cuisine.MEXICAN.ordinal,
            mealType = MealType.SIDE_DISH.ordinal,
            ingredients = listOf("Tortilla", "Beans"),
            intolerances = listOf(Intolerance.SESAME, Intolerance.WHEAT).map { it.ordinal },
            diets = listOf(Diet.VEGAN, Diet.VEGETARIAN).map { it.ordinal },
            minCalories = 100,
            maxCalories = 500,
            minCarbs = 10,
            maxCarbs = 100,
            minFat = 5,
            maxFat = 50,
            minProtein = 5,
            maxProtein = 50
        )

        // given information for a new recipe (in companion object)
        // mocks
        whenever(pictureDomainMock.generatePictureName()).thenReturn(recipePicturesNames.first())
        whenever(jdbiRecipeRepositoryMock.searchRecipes(userId, form))
            .thenReturn(listOf(JdbiRecipeModel()))
     */
}