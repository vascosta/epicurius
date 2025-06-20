package epicurius.integration.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SearchRecipesIntegrationTests : RecipeIntegrationTest() {

    @BeforeEach
    fun setupSearchRecipesIntegrationTests() {
        testRecipe = createRecipe(
            testAuthor.token,
            body = createRecipeInputModel,
            pictures = listOf(testPicture),
        )?.recipe!!
    }

    private val testPrivateAuthor = createTestUser(tm, true)
    private val newUser = createTestUser(tm)

    private val limit = 10

    @Test
    fun `Should search for recipes by name with code 200`() {
        // given an authenticated user and a recipe name

        // when searching for recipes by name
        val response = searchRecipes(
            testUser.token,
            name = "Pastel",
            lastRecipeId = null,
            limit = limit
        )

        // then the response contains the expected recipe info
        assertNotNull(response)
        assertTrue(response.recipes.isNotEmpty())
        assertTrue(response.recipes.any { it.name.contains("Pastel") })
    }

    @Test
    fun `Should search for a recipe according to user's intolerances with code 200`() {
        // given an authenticated user with specific intolerances

        // when searching for recipes by user intolerances
        val responseWithUserIntolerances = searchRecipes(
            testUser.token,
            intolerances = listOf(Intolerance.EGG, Intolerance.DAIRY),
            lastRecipeId = null,
            limit = limit
        )

        // then the response an empty list of recipes that match the user's intolerances
        assertNotNull(responseWithUserIntolerances)
        assertTrue(responseWithUserIntolerances.recipes.isEmpty())

        // when searching for recipes with intolerances and diets that are the same as the user's
        val responseWithoutUserIntolerances = searchRecipes(
            testUser.token,
            intolerances = listOf(Intolerance.SEAFOOD),
            lastRecipeId = null,
            limit = limit
        )

        // then the response contains recipes that match the search criteria
        assertNotNull(responseWithoutUserIntolerances)
        assertTrue(responseWithoutUserIntolerances.recipes.isNotEmpty())
        assertTrue(responseWithoutUserIntolerances.recipes.any { it.name.contains("Pastel") })
    }

    @Test
    fun `Should search for a recipe without ingredients with code 200`() {
        // given an authenticated user and a search input without ingredients

        // when searching for recipes without ingredients
        val response = searchRecipes(
            testUser.token,
            name = "Pastel",
            diets = listOf(Diet.LACTO_VEGETARIAN, Diet.OVO_VEGETARIAN),
            cuisine = Cuisine.MEDITERRANEAN,
            mealType = MealType.DESSERT,
            lastRecipeId = null,
            limit = limit
        )

        // then the response contains recipes that match the search criteria
        assertNotNull(response)
        assertTrue(response.recipes.isNotEmpty())
        assertTrue(response.recipes.any { it.name.contains("Pastel") })
        assertTrue(response.recipes.all { it.cuisine == Cuisine.MEDITERRANEAN })
        assertTrue(response.recipes.all { it.mealType == MealType.DESSERT })
    }

    @Test
    fun `Should search for a recipe with ingredients with code 200`() {
        // given an authenticated user and a search input with ingredients

        // when searching for recipes with ingredients
        val response = searchRecipes(
            testUser.token,
            name = "Pastel",
            ingredients = listOf("flour", "sugar"),
            lastRecipeId = null,
            limit = limit
        )

        // then the response contains recipes that match the search criteria
        assertNotNull(response)
        assertTrue(response.recipes.isNotEmpty())
        assertTrue(response.recipes.any { it.name.contains("Pastel") })
    }

    @Test
    fun `Should search for recipes of public users with code 200`() {
        // given an authenticated user and a search input for public users

        // when searching for recipes of public users
        val response = searchRecipes(
            testUser.token,
            name = "Pastel",
            lastRecipeId = null,
            limit = limit
        )

        // then the response contains recipes that match the search criteria
        assertNotNull(response)
        assertTrue(response.recipes.isNotEmpty())
        assertTrue(response.recipes.any { it.name.contains("Pastel") })
    }

    @Test
    fun `Should search for recipes from private users when not followed with code 200`() {
        // given an authenticated user and a private recipe from a user not followed by the authenticated user
        val testPrivateRecipe = createRecipe(
            testPrivateAuthor.token,
            body = createRecipeInputModel.copy("Private Recipe"),
            pictures = listOf(testPicture),
        )?.recipe!!

        // when searching for recipes from private users
        val response = searchRecipes(
            testUser.token,
            name = testPrivateRecipe.name,
            lastRecipeId = null,
            limit = limit
        )

        // then the response does not contain the private recipe
        assertNotNull(response)
        assertTrue(response.recipes.isEmpty())
    }

    @Test
    fun `Should search for recipes from private users when followed with code 200`() {
        // given an authenticated user and a private recipe from a user followed by the authenticated user
        val testPrivateRecipe = createRecipe(
            testPrivateAuthor.token,
            body = createRecipeInputModel.copy("Private Recipe"),
            pictures = listOf(testPicture),
        )?.recipe!!
        follow(newUser.token, testPrivateAuthor.user.name)
        acceptFollowRequest(testPrivateAuthor.token, newUser.user.name)

        // when searching for recipes from private user
        val response = searchRecipes(
            newUser.token,
            name = testPrivateRecipe.name.replace(" ", "-"),
            lastRecipeId = null,
            limit = limit
        )

        // then the response contains the private recipe
        assertNotNull(response)
        assertTrue(response.recipes.isNotEmpty())
        assertTrue(response.recipes.any { it.name == testPrivateRecipe.name })
    }
}
