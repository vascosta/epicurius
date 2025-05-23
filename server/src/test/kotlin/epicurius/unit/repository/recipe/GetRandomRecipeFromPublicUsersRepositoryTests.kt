package epicurius.unit.repository.recipe

import epicurius.domain.recipe.MealType
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetRandomRecipeFromPublicUsersRepositoryTests : RecipeRepositoryTest() {

    @Test
    fun `Should retrieve a random recipe from public users`() {
        // given a user public user (testUser)

        // when retrieving a random recipe from public users
        val retrievedRecipe = getRandomRecipesFromPublicUsers(
            MealType.DESSERT,
            testUserPublic.user.intolerances,
            testUserPublic.user.diets,
            1
        )

        // then the recipe is retrieved successfully
        assertNotNull(retrievedRecipe)
        assertTrue(retrievedRecipe.isNotEmpty())
    }
}
