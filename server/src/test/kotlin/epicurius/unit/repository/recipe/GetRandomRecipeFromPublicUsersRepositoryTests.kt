package epicurius.unit.repository.recipe

import epicurius.domain.recipe.MealType
import kotlin.test.Test
import kotlin.test.assertTrue

class GetRandomRecipeFromPublicUsersRepositoryTests : RecipeRepositoryTest() {

    @Test
    fun `Should retrieve a random recipe from public users successfully`() {
        // given a user

        // when retrieving a random recipe from public users
        val retrievedRecipes = getRandomRecipesFromPublicUsers(
            testUserPrivate.user.id,
            MealType.DESSERT,
            testUserPublic.user.intolerances,
            testUserPublic.user.diets,
            1
        )

        // then a list of recipes is returned successfully
        assertTrue(retrievedRecipes.isNotEmpty())
    }
}
