package epicurius.unit.repository.recipe

import epicurius.domain.recipe.MealType
import kotlin.test.Test
import kotlin.test.assertNotNull

class GetRandomRecipeFromPublicUsersRepositoryTests: RecipeRepositoryTest() {

    @Test
    fun `Should retrieve a random recipe from public users`() {
        // given a user public user (testUser)

        // when retrieving a random recipe from the followed users
        val retrievedRecipe = getRandomRecipeFromPublicUsers(MealType.DESSERT)

        // then the recipe is retrieved successfully
        assertNotNull(retrievedRecipe) // there is at least one recipe with mealType DESSERT (testRecipe)
    }
}