package epicurius.unit.repository.collection

import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.utils.createTestRecipe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AddRecipeToCollectionRepositoryTests : CollectionRepositoryTest() {

    private val testRecipe = createTestRecipe(tm, fs, testOwner.user)
    private val testRecipeInfo = JdbiRecipeInfo(
        testRecipe.id,
        testRecipe.name,
        testRecipe.cuisine,
        testRecipe.mealType,
        testRecipe.preparationTime,
        testRecipe.servings,
        listOf("test-picture.jpeg")
    )

    @Test
    fun `Should add a recipe to a collection and then delete it successfully`() {
        // given a collection (testCollectionId) and a recipe (testRecipe)
        val collectionId = testCollectionId
        val recipeId = testRecipe.id

        // when adding the recipe to the collection
        val collectionWithRecipe = addRecipeToCollection(collectionId, recipeId)

        // then the recipe is added successfully
        assertNotNull(collectionWithRecipe)
        assertEquals(testRecipeInfo, collectionWithRecipe.recipes.first())

        // when removing the recipe from the collection
        val collectionWithoutRecipe = removeRecipeFromCollection(collectionId, recipeId)

        // then the recipe is removed successfully
        assertNotNull(collectionWithoutRecipe)
        assertTrue(collectionWithoutRecipe.recipes.isEmpty())
    }
}
