package epicurius.unit.services.menu

import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.MealType
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetDailyMenuServiceTests : MenuServiceTest() {

    @Test
    fun `Should retrieve the daily menu for a given user successfully`() {
        // given a user (userIntolerances, userDiets)

        // mock
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.BREAKFAST, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicBreakfastJdbiRecipeModel))
        whenever(
            pictureRepositoryMock.getPicture(publicBreakfastJdbiRecipeModel.picturesNames.first(), RECIPES_FOLDER)
        ).thenReturn(byteArrayOf())
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.SOUP, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicSoupJdbiRecipeModel))
        whenever(
            pictureRepositoryMock.getPicture(publicSoupJdbiRecipeModel.picturesNames.first(), RECIPES_FOLDER)
        ).thenReturn(byteArrayOf())
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.DESSERT, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicDessertJdbiRecipeModel))
        whenever(
            pictureRepositoryMock.getPicture(publicDessertJdbiRecipeModel.picturesNames.first(), RECIPES_FOLDER)
        ).thenReturn(byteArrayOf())
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.MAIN_COURSE, userIntolerances, userDiets, 2)
        ).thenReturn(listOf(publicLunchJdbiRecipeModel, publicDinnerJdbiRecipeModel2))
        whenever(
            pictureRepositoryMock.getPicture(publicLunchJdbiRecipeModel.picturesNames.first(), RECIPES_FOLDER)
        ).thenReturn(byteArrayOf())
        whenever(
            pictureRepositoryMock.getPicture(publicDinnerJdbiRecipeModel2.picturesNames.first(), RECIPES_FOLDER)
        ).thenReturn(byteArrayOf())

        // when retrieving the daily menu
        val dailyMenu = menuService.getDailyMenu(userIntolerances, userDiets)
        val breakfast = dailyMenu["breakfast"]
        val soup = dailyMenu["soup"]
        val dessert = dailyMenu["dessert"]
        val lunch = dailyMenu["lunch"]
        val dinner = dailyMenu["dinner"]

        // then the menu is retrieved successfully
        assertNotNull(breakfast)
        assertNotNull(soup)
        assertNotNull(dessert)
        assertNotNull(lunch)
        assertNotNull(dinner)
        assertEquals(testDailyMenu["breakfast"]!!.first().id, breakfast.id)
        assertEquals(testDailyMenu["soup"]!!.first().id, soup.id)
        assertEquals(testDailyMenu["dessert"]!!.first().id, dessert.id)
        assertEquals(testDailyMenu["lunch"]!!.first().id, lunch.id)
        assertEquals(testDailyMenu["dinner"]!!.first().id, dinner.id)
    }

    @Test
    fun `Should retrieve a daily menu with nulls when there is no recipes matching the user intolerances and diets`() {
        // given a user (userIntolerances, userDiets)

        // mock
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.BREAKFAST, userIntolerances, userDiets, 1)
        ).thenReturn(emptyList())
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.SOUP, userIntolerances, userDiets, 1)
        ).thenReturn(emptyList())
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.DESSERT, userIntolerances, userDiets, 1)
        ).thenReturn(emptyList())
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.MAIN_COURSE, userIntolerances, userDiets, 2)
        ).thenReturn(emptyList())

        // when retrieving the daily menu
        val dailyMenu = menuService.getDailyMenu(userIntolerances, userDiets)
        val breakfast = dailyMenu["breakfast"]
        val soup = dailyMenu["soup"]
        val dessert = dailyMenu["dessert"]
        val lunch = dailyMenu["lunch"]
        val dinner = dailyMenu["dinner"]

        // then the menu contains nulls
        assertNull(breakfast)
        assertNull(soup)
        assertNull(dessert)
        assertNull(lunch)
        assertNull(dinner)
    }

    @Test
    fun `Should retrieve the daily menu without dinner for a given user successfully`() {
        // given a user (userIntolerances, userDiets)

        // mock
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.BREAKFAST, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicBreakfastJdbiRecipeModel))
        whenever(
            pictureRepositoryMock.getPicture(publicBreakfastJdbiRecipeModel.picturesNames.first(), RECIPES_FOLDER)
        ).thenReturn(byteArrayOf())
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.SOUP, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicSoupJdbiRecipeModel))
        whenever(
            pictureRepositoryMock.getPicture(publicSoupJdbiRecipeModel.picturesNames.first(), RECIPES_FOLDER)
        ).thenReturn(byteArrayOf())
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.DESSERT, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicDessertJdbiRecipeModel))
        whenever(
            pictureRepositoryMock.getPicture(publicDessertJdbiRecipeModel.picturesNames.first(), RECIPES_FOLDER)
        ).thenReturn(byteArrayOf())
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.MAIN_COURSE, userIntolerances, userDiets, 2)
        ).thenReturn(listOf(publicLunchJdbiRecipeModel))
        whenever(
            pictureRepositoryMock.getPicture(publicLunchJdbiRecipeModel.picturesNames.first(), RECIPES_FOLDER)
        ).thenReturn(byteArrayOf())

        // when retrieving the daily menu
        val dailyMenu = menuService.getDailyMenu(userIntolerances, userDiets)
        val breakfast = dailyMenu["breakfast"]
        val soup = dailyMenu["soup"]
        val dessert = dailyMenu["dessert"]
        val lunch = dailyMenu["lunch"]
        val dinner = dailyMenu["dinner"]

        // then the menu is null
        assertNotNull(breakfast)
        assertNotNull(soup)
        assertNotNull(dessert)
        assertNotNull(lunch)
        assertNull(dinner)
        assertEquals(testDailyMenu["breakfast"]!!.first().id, breakfast.id)
        assertEquals(testDailyMenu["soup"]!!.first().id, soup.id)
        assertEquals(testDailyMenu["dessert"]!!.first().id, dessert.id)
        assertEquals(testDailyMenu["lunch"]!!.first().id, lunch.id)
    }
}
