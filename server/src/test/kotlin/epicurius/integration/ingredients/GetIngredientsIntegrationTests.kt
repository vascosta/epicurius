package epicurius.integration.ingredients

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetIngredientsIntegrationTests: IngredientsIntegrationTest() {

    private val testUser = createTestUser(tm)

    private val testIngredients = listOf(
        "apple",
        "applesauce",
        "apple juice",
        "apple cider",
        "apple jelly",
        "apple butter",
        "apple pie spice",
        "apple pie filling",
        "apple cider vinegar",
        "applewood smoked bacon"
    )

    @Test
    fun `Should retrieve ingredients given a partial name with code 200`() {
        // given a partial name
        val partialName = "app"

        // when retrieving the ingredients
        val body = getIngredients(testUser.token, partialName)

        // then the products list is retrieved successfully with code 200
        assertNotNull(body)
        assertEquals(testIngredients, body.ingredients)
    }

    @Test
    fun `Should retrieve an empty list of ingredients when no ingredients are found with code 200`() {
        // given a partial name
        val nonExistingPartialName = "nonexistent"

        // when retrieving the ingredients
        val body = getIngredients(testUser.token, nonExistingPartialName)

        // then the products list is retrieved successfully with code 200
        assertNotNull(body)
        assertEquals(emptyList<String>(), body.ingredients)
    }
}