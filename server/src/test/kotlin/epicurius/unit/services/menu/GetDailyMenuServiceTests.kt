package epicurius.unit.services.menu

import epicurius.domain.recipe.MealType
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetDailyMenuServiceTests: MenuServiceTest() {

    @Test
    fun `Should retrieve the daily menu for a given user successfully`() {
        // given a public author and a user (PUBLIC_AUTHOR_ID, userIntolerances, userDiets)

        // mock
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.BREAKFAST, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicBreakfastJdbiRecipeModel))
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.SOUP, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicSoupJdbiRecipeModel))
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.DESSERT, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicDessertJdbiRecipeModel))
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.MAIN_COURSE, userIntolerances, userDiets, 2)
        ).thenReturn(listOf(publicMainCourseJdbiRecipeModel, publicMainCourseJdbiRecipeModel2))

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
        assertTrue(testDailyMenu["breakfast"]!!.contains(breakfast))
        assertTrue(testDailyMenu["soup"]!!.contains(soup))
        assertTrue(testDailyMenu["dessert"]!!.contains(dessert))
        assertTrue(testDailyMenu["lunch"]!!.contains(lunch))
        assertTrue(testDailyMenu["dinner"]!!.contains(dinner))
    }

    @Test
    fun `Should retrieve a daily menu with nulls when there is no recipes matching the user intolerances and diets`() {
        // given a public author and a user (PUBLIC_AUTHOR_ID, userIntolerances, userDiets)

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
        // given a public author and a user (PUBLIC_AUTHOR_ID, userIntolerances, userDiets)

        // mock
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.BREAKFAST, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicBreakfastJdbiRecipeModel))
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.SOUP, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicSoupJdbiRecipeModel))
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.DESSERT, userIntolerances, userDiets, 1)
        ).thenReturn(listOf(publicDessertJdbiRecipeModel))
        whenever(
            jdbiRecipeRepositoryMock.getRandomRecipesFromPublicUsers(MealType.MAIN_COURSE, userIntolerances, userDiets, 2)
        ).thenReturn(listOf(publicMainCourseJdbiRecipeModel))


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
    }
}