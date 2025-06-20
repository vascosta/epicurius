package epicurius.integration.recipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.get
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetRecipeIntegrationTests : RecipeIntegrationTest() {

    private val testPrivateAuthor = createTestUser(tm, true)
    private val testPrivateRecipe = createTestRecipe(tm, testPrivateAuthor.user)

    @Test
    fun `Should the author retrieve the recipe with code 201`() {
        // given an authenticated user who is the author of the recipe and a recipe id

        // when retrieving the recipe
        val response = getRecipe(testAuthor.token, testRecipe.id)

        // then the recipe is retrieved successfully
        assertNotNull(response)
        assertEquals(testRecipe.id, response.recipe.id)
        assertEquals(testRecipe.name, response.recipe.name)
        assertEquals(testRecipe.description, response.recipe.description)
        assertEquals(testRecipe.servings, response.recipe.servings)
        assertEquals(testRecipe.preparationTime, response.recipe.preparationTime)
        assertEquals(testRecipe.cuisine, response.recipe.cuisine)
        assertEquals(testRecipe.mealType, response.recipe.mealType)
        assertTrue(response.recipe.intolerances.containsAll(testRecipe.intolerances))
        assertTrue(response.recipe.diets.containsAll(testRecipe.diets))
        assertEquals(testRecipe.ingredients, response.recipe.ingredients)
        assertEquals(testRecipe.calories, response.recipe.calories)
        assertEquals(testRecipe.protein, response.recipe.protein)
        assertEquals(testRecipe.fat, response.recipe.fat)
        assertEquals(testRecipe.carbs, response.recipe.carbs)
        assertEquals(testRecipe.instructions, response.recipe.instructions)
        assertEquals(1, response.recipe.pictures.size)
        assertEquals(false, response.recipe.isInCollection)
    }

    @Test
    fun `Should a follower of the private author retrieve the recipe with code 200`() {
        // given an authenticated user who is a follower of the author and a recipe id
        follow(testUser.token, testPrivateAuthor.user.name)
        acceptFollowRequest(testPrivateAuthor.token, testUser.user.name)

        // when retrieving the recipe
        val response = getRecipe(testUser.token, testPrivateRecipe.id)

        // then the recipe is retrieved successfully
        assertNotNull(response)
        assertEquals(testPrivateRecipe.id, response.recipe.id)
        assertEquals(testPrivateRecipe.name, response.recipe.name)
        assertEquals(testPrivateRecipe.description, response.recipe.description)
        assertEquals(testPrivateRecipe.servings, response.recipe.servings)
        assertEquals(testPrivateRecipe.preparationTime, response.recipe.preparationTime)
        assertEquals(testPrivateRecipe.cuisine, response.recipe.cuisine)
        assertEquals(testPrivateRecipe.mealType, response.recipe.mealType)
        assertTrue(response.recipe.intolerances.containsAll(testPrivateRecipe.intolerances))
        assertTrue(response.recipe.diets.containsAll(testPrivateRecipe.diets))
        assertEquals(testPrivateRecipe.ingredients, response.recipe.ingredients)
        assertEquals(testPrivateRecipe.calories, response.recipe.calories)
        assertEquals(testPrivateRecipe.protein, response.recipe.protein)
        assertEquals(testPrivateRecipe.fat, response.recipe.fat)
        assertEquals(testPrivateRecipe.carbs, response.recipe.carbs)
        assertEquals(testPrivateRecipe.instructions, response.recipe.instructions)
        assertEquals(1, response.recipe.pictures.size)
        assertEquals(false, response.recipe.isInCollection)
    }

    @Test
    fun `Should retrieve a recipe successfully when the user is not following the public author with code 200`() {
        // given an authenticated user who is not following the author and a recipe id

        // when retrieving the recipe
        val response = getRecipe(testUser.token, testRecipe.id)

        // then the recipe is retrieved successfully
        assertNotNull(response)
        assertEquals(testRecipe.id, response.recipe.id)
        assertEquals(testRecipe.name, response.recipe.name)
        assertEquals(testRecipe.description, response.recipe.description)
        assertEquals(testRecipe.servings, response.recipe.servings)
        assertEquals(testRecipe.preparationTime, response.recipe.preparationTime)
        assertEquals(testRecipe.cuisine, response.recipe.cuisine)
        assertEquals(testRecipe.mealType, response.recipe.mealType)
        assertTrue(response.recipe.intolerances.containsAll(testRecipe.intolerances))
        assertTrue(response.recipe.diets.containsAll(testRecipe.diets))
        assertEquals(testRecipe.ingredients, response.recipe.ingredients)
        assertEquals(testRecipe.calories, response.recipe.calories)
        assertEquals(testRecipe.protein, response.recipe.protein)
        assertEquals(testRecipe.fat, response.recipe.fat)
        assertEquals(testRecipe.carbs, response.recipe.carbs)
        assertEquals(testRecipe.instructions, response.recipe.instructions)
        assertEquals(1, response.recipe.pictures.size)
        assertEquals(false, response.recipe.isInCollection)
    }

    @Test
    fun `Should fail with code 401 when retrieving a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // when retrieving the recipe
        val error = get<Problem>(
            client,
            api(Uris.Recipe.RECIPE.replace("{id}", nonExistingRecipeId.toString())),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )
        assertNotNull(error)

        // then the recipe is not retrieved
        assertEquals(RecipeNotFound().message, error.detail)
    }

    @Test
    fun `Should fail with code 403 when retrieving a recipe from a private user not followed`() {
        // given a user not following the author and a recipe id

        // when retrieving the recipe
        val error = get<Problem>(
            client,
            api(Uris.Recipe.RECIPE.replace("{id}", testPrivateRecipe.id.toString())),
            responseStatus = HttpStatus.FORBIDDEN,
            token = testAuthor.token
        )
        assertNotNull(error)

        // then the recipe is not retrieved
        assertEquals(RecipeNotAccessible().message, error.detail)
    }
}
