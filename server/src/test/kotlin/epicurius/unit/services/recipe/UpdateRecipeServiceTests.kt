package epicurius.unit.services.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.InvalidIngredient
import epicurius.domain.exceptions.NotTheAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.MealType
import epicurius.http.recipe.models.input.UpdateRecipeInputModel
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.utils.generateRandomRecipeDescription
import epicurius.utils.generateRandomRecipeIngredients
import epicurius.utils.generateRandomRecipeInstructions
import epicurius.utils.generateRandomRecipeName
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateRecipeServiceTests : RecipeServiceTest() {

    private val updateRecipeInputInfo = UpdateRecipeInputModel(
        generateRandomRecipeName(),
        generateRandomRecipeDescription(),
        1,
        1,
        Cuisine.ASIAN,
        MealType.SOUP,
        setOf(Intolerance.PEANUT),
        setOf(Diet.KETOGENIC),
        generateRandomRecipeIngredients(),
        1,
        1,
        1,
        1,
        generateRandomRecipeInstructions()
    )

    @Test
    fun `Should update a recipe successfully`() {
        // given information to update a recipe
        val jdbiUpdateRecipeInfo = updateRecipeInputInfo.toJdbiUpdateRecipeModel(RECIPE_ID, null)
        val firestoreUpdateRecipeInfo = updateRecipeInputInfo.toFirestoreUpdateRecipeModel(RECIPE_ID)

        // mock
        val mockJdbiRecipeModel = JdbiRecipeModel(
            RECIPE_ID,
            updateRecipeInputInfo.name!!,
            AUTHOR_ID,
            authorName,
            jdbiCreateRecipeInfo.date,
            1,
            1,
            Cuisine.ASIAN,
            MealType.SOUP,
            listOf(Intolerance.PEANUT),
            listOf(Diet.KETOGENIC),
            updateRecipeInputInfo.ingredients!!,
            1,
            1,
            1,
            1,
            recipePicturesNames,
        )
        val mockFirestoreRecipeModel = FirestoreRecipeModel(
            RECIPE_ID,
            updateRecipeInputInfo.description!!,
            updateRecipeInputInfo.instructions!!
        )
        updateRecipeInputInfo.ingredients?.forEach { ingredient ->
            whenever(
                runBlocking {
                    spoonacularRepositoryMock.getProductsList(ingredient.name.lowercase())
                }
            ).thenReturn(listOf(ingredient.name.lowercase()))
        }
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(mockJdbiRecipeModel)
        whenever(jdbiRecipeRepositoryMock.updateRecipe(jdbiUpdateRecipeInfo)).thenReturn(mockJdbiRecipeModel)
        whenever(runBlocking { firestoreRecipeRepositoryMock.updateRecipe(firestoreUpdateRecipeInfo) }).thenReturn(mockFirestoreRecipeModel)

        // when updating the recipe
        val updatedRecipe = runBlocking { updateRecipe(AUTHOR_ID, RECIPE_ID, updateRecipeInputInfo) }

        // then the recipe is updated successfully
        assertEquals(RECIPE_ID, updatedRecipe.id)
        assertEquals(updateRecipeInputInfo.name, updatedRecipe.name)
        assertEquals(authorName, updatedRecipe.authorUsername)
        assertEquals(updateRecipeInputInfo.description, updatedRecipe.description)
        assertEquals(updateRecipeInputInfo.servings, updatedRecipe.servings)
        assertEquals(updateRecipeInputInfo.preparationTime, updatedRecipe.preparationTime)
        assertEquals(updateRecipeInputInfo.cuisine, updatedRecipe.cuisine)
        assertEquals(updateRecipeInputInfo.mealType, updatedRecipe.mealType)
        assertEquals(updateRecipeInputInfo.intolerances?.toList(), updatedRecipe.intolerances)
        assertEquals(updateRecipeInputInfo.diets?.toList(), updatedRecipe.diets)
        assertEquals(updateRecipeInputInfo.ingredients, updatedRecipe.ingredients)
        assertEquals(updateRecipeInputInfo.calories, updatedRecipe.calories)
        assertEquals(updateRecipeInputInfo.protein, updatedRecipe.protein)
        assertEquals(updateRecipeInputInfo.fat, updatedRecipe.fat)
        assertEquals(updateRecipeInputInfo.carbs, updatedRecipe.carbs)
        assertEquals(updateRecipeInputInfo.instructions, updatedRecipe.instructions)
    }

    @Test
    fun `Should throw RecipeNotFound exception when updating a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        updateRecipeInputInfo.ingredients?.forEach { ingredient ->
            whenever(
                runBlocking {
                    spoonacularRepositoryMock.getProductsList(ingredient.name.lowercase())
                }
            ).thenReturn(listOf(ingredient.name.lowercase()))
        }
        whenever(jdbiRecipeRepositoryMock.getRecipe(nonExistingRecipeId)).thenReturn(null)

        // when updating the recipe
        // then the recipe is not updated and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> {
            runBlocking { updateRecipe(AUTHOR_ID, nonExistingRecipeId, updateRecipeInputInfo) }
        }
    }

    @Test
    fun `Should throw NotTheAuthor exception when updating a recipe that does not belong to the user`() {
        // given a user id and a recipe id (RECIPE_ID) that does not belong to him
        val userId = 9999

        // mock
        updateRecipeInputInfo.ingredients?.forEach { ingredient ->
            whenever(
                runBlocking {
                    spoonacularRepositoryMock.getProductsList(ingredient.name.lowercase())
                }
            ).thenReturn(listOf(ingredient.name.lowercase()))
        }
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(jdbiRecipeModel)

        // when updating the recipe
        // then the recipe is not updated and throws NotTheAuthor exception
        assertFailsWith<NotTheAuthor> {
            runBlocking { updateRecipe(userId, RECIPE_ID, updateRecipeInputInfo) }
        }
    }

    @Test
    fun `Should throw InvalidIngredient exception when updating a recipe with an invalid ingredients`() {
        // given an invalid ingredient
        val invalidIngredient = Ingredient("invalid", 1, IngredientUnit.G)

        // mock
        whenever(
            runBlocking {
                spoonacularRepositoryMock.getProductsList(invalidIngredient.name)
            }
        ).thenReturn(emptyList())

        // when updating the recipe
        // then the recipe is not updated and throws InvalidIngredient exception
        assertFailsWith<InvalidIngredient> {
            runBlocking {
                updateRecipe(AUTHOR_ID, RECIPE_ID, updateRecipeInputInfo.copy(ingredients = listOf(invalidIngredient)))
            }
        }
    }
}
