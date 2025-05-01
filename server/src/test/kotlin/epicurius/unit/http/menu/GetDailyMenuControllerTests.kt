package epicurius.unit.http.menu

import epicurius.domain.recipe.RecipeInfo
import epicurius.http.menu.models.out.GetDailyMenuOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDailyMenuControllerTests : MenuHttpTest() {

    @Test
    fun `Should retrieve the daily menu for a given user successfully`() {
        // given a user (testAuthenticatedUser)

        // mock
        whenever(menuServiceMock.getDailyMenu(testAuthenticatedUser.user.intolerances, testAuthenticatedUser.user.diets))
            .thenReturn(testDailyMenu)

        // when retrieving the daily menu
        val response = getDailyMenu(testAuthenticatedUser)
        val body = response.body as GetDailyMenuOutputModel

        // then the menu is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(testDailyMenu, body.menu)
    }

    @Test
    fun `Should retrieve a daily menu with nulls when there is no recipes matching the user intolerances and diets`() {
        // given a user (testAuthenticatedUser)

        // mock
        val mockDailyMenu = mapOf<String, RecipeInfo?>(
            "breakfast" to null,
            "soup" to null,
            "dessert" to null,
            "lunch" to null,
            "dinner" to null
        )
        whenever(menuServiceMock.getDailyMenu(testAuthenticatedUser.user.intolerances, testAuthenticatedUser.user.diets))
            .thenReturn(mockDailyMenu)

        // when retrieving the daily menu
        val response = getDailyMenu(testAuthenticatedUser)
        val body = response.body as GetDailyMenuOutputModel

        // then the menu contains nulls
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockDailyMenu, body.menu)
    }

    @Test
    fun `Should retrieve the daily menu without dinner for a given user successfully`() {
        // given a user (testAuthenticatedUser)

        // mock
        val mockDailyMenu = mapOf<String, RecipeInfo?>(
            "breakfast" to publicBreakfastRecipeInfo,
            "soup" to publicSoupRecipeInfo,
            "dessert" to publicDessertRecipeInfo,
            "lunch" to publicBreakfastRecipeInfo,
            "dinner" to null
        )
        whenever(menuServiceMock.getDailyMenu(testAuthenticatedUser.user.intolerances, testAuthenticatedUser.user.diets))
            .thenReturn(mockDailyMenu)

        // when retrieving the daily menu
        val response = getDailyMenu(testAuthenticatedUser)
        val body = response.body as GetDailyMenuOutputModel

        // then the menu is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockDailyMenu, body.menu)
    }
}
