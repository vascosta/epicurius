package epicurius.integration.menu

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetDailyMenuIntegrationTests : MenuIntegrationTest() {

    private val testUser = createTestUser(tm)
    private val testRecipe = createTestRecipe(tm, fs, testUser.user) // dessert recipe

    @Test
    fun `Should retrieve the daily menu for a given user with code 200`() {
        // given a user (testUser)

        // when retrieving the daily menu
        val body = getDailyMenu(testUser.token)

        // then the menu is retrieved successfully with code 200
        assertNotNull(body)
        val dessert = body.menu["dessert"]
        assertNotNull(dessert)
    }

    @Test
    fun `Should retrieve a daily menu with nulls when there is no recipes matching the user intolerances and diets with code 200`() {
        // given a user with intolerances and diets that do not match any recipes
        val user = createTestUser(tm)
        tm.run {
            it.userRepository.updateUser(
                user.user.id,
                JdbiUpdateUserModel(intolerances = listOf(Intolerance.SULFITE.ordinal), diets = listOf(Diet.LOW_FODMAP.ordinal))
            )
        }

        // when retrieving the daily menu
        val body = getDailyMenu(user.token)

        // then the menu is retrieved with nulls for all recipes successfully with code 200
        assertNotNull(body)
        assertTrue(body.menu.all { it.value == null })
    }
}
