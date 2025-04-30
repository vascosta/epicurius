package epicurius.unit.repository.recipe

import epicurius.domain.recipe.MealType
import epicurius.domain.user.FollowingStatus
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetRandomRecipeFromFollowingRepositoryTests : RecipeRepositoryTest() {

    @Test
    fun `Should retrieve a random recipe from users being followed by the user`() {
        // given a user that follows another user (testUser, testAuthor) and a recipe created by the followed user (testRecipe)
        tm.run { it.userRepository.follow(testUser.id, testAuthor.id, FollowingStatus.ACCEPTED.ordinal) }

        // when retrieving a random recipe from the followed users
        val retrievedRecipe = getRandomRecipesFromFollowing(testUser.id, MealType.DESSERT, 1)

        // then the recipe is retrieved successfully
        assertNotNull(retrievedRecipe)
        assertTrue(retrievedRecipe.isNotEmpty())
    }
}
