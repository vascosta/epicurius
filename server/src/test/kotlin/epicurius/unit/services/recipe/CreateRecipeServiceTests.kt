package epicurius.unit.services.recipe

import epicurius.domain.exceptions.InvalidIngredient
import epicurius.domain.exceptions.InvalidNumberOfRecipePictures
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.multipart.MultipartFile
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateRecipeServiceTests : RecipeServiceTest() {

    @Test
    fun `Should create a recipe successfully`() {
        // given information for a new recipe (jdbiCreateRecipeInfo, firestoreRecipeInfo)

        // mock
        jdbiCreateRecipeInfo.ingredients.forEach { ingredient ->
            whenever(
                runBlocking {
                    spoonacularRepositoryMock.getIngredients(ingredient.name.lowercase())
                }
            ).thenReturn(listOf(ingredient.name.lowercase()))
        }
        whenever(pictureDomainMock.generatePictureName()).thenReturn(recipePicturesNames.first())
        whenever(
            jdbiRecipeRepositoryMock.createRecipe(
                argThat { model ->
                    model.name == createRecipeInputInfo.name &&
                        model.authorId == AUTHOR_ID &&
                        model.servings == createRecipeInputInfo.servings &&
                        model.preparationTime == createRecipeInputInfo.preparationTime &&
                        model.cuisine == createRecipeInputInfo.cuisine.ordinal &&
                        model.mealType == createRecipeInputInfo.mealType.ordinal &&
                        model.intolerances == createRecipeInputInfo.intolerances.map { it.ordinal } &&
                        model.diets == createRecipeInputInfo.diets.map { it.ordinal } &&
                        model.ingredients == createRecipeInputInfo.ingredients &&
                        model.calories == createRecipeInputInfo.calories &&
                        model.protein == createRecipeInputInfo.protein &&
                        model.fat == createRecipeInputInfo.fat &&
                        model.carbs == createRecipeInputInfo.carbs &&
                        model.picturesNames == recipePicturesNames
                }
            )
        ).thenReturn(RECIPE_ID)

        // when creating the recipe
        val recipe = runBlocking { createRecipe(AUTHOR_ID, authorUsername, createRecipeInputInfo, recipePictures) }

        // then the recipe is created successfully
        verify(firestoreRecipeRepositoryMock).createRecipe(firestoreRecipeInfo)
        verify(pictureRepositoryMock).updatePicture(recipePicturesNames.first(), recipePictures.first(), RECIPES_FOLDER)
        assertEquals(RECIPE_ID, recipe.id)
        assertEquals(createRecipeInputInfo.name, recipe.name)
        assertEquals(authorUsername, recipe.authorUsername)
        assertEquals(createRecipeInputInfo.description, recipe.description)
        assertEquals(createRecipeInputInfo.servings, recipe.servings)
        assertEquals(createRecipeInputInfo.preparationTime, recipe.preparationTime)
        assertEquals(createRecipeInputInfo.cuisine, recipe.cuisine)
        assertEquals(createRecipeInputInfo.mealType, recipe.mealType)
        assertEquals(createRecipeInputInfo.intolerances.toList(), recipe.intolerances)
        assertEquals(createRecipeInputInfo.diets.toList(), recipe.diets)
        assertEquals(createRecipeInputInfo.ingredients, recipe.ingredients)
        assertEquals(createRecipeInputInfo.calories, recipe.calories)
        assertEquals(createRecipeInputInfo.protein, recipe.protein)
        assertEquals(createRecipeInputInfo.fat, recipe.fat)
        assertEquals(createRecipeInputInfo.carbs, recipe.carbs)
        assertEquals(createRecipeInputInfo.instructions, recipe.instructions)
        assertContentEquals(recipePictures.map { it.bytes }, recipe.pictures)
    }

    @Test
    fun `Should throw InvalidNumberOfRecipePictures exception when creating a recipe with an invalid number of pictures`() {
        // given an invalid number of pictures
        val invalidPictures = emptySet<MultipartFile>()

        // when creating the recipe with invalid number of pictures
        // then the recipe is not created and throws InvalidNumberOfRecipePictures exception
        assertFailsWith<InvalidNumberOfRecipePictures> {
            runBlocking {
                createRecipe(AUTHOR_ID, authorUsername, createRecipeInputInfo, invalidPictures)
            }
        }
    }

    @Test
    fun `Should throw InvalidIngredient exception when creating a recipe with an invalid ingredient`() {
        // given an invalid ingredient
        val invalidIngredient = Ingredient("invalid", 1.0, IngredientUnit.G)

        // mock
        whenever(
            runBlocking {
                spoonacularRepositoryMock.getIngredients(invalidIngredient.name)
            }
        ).thenReturn(emptyList())

        // when creating the recipe with an invalid ingredient
        // then the recipe is not created and throws InvalidIngredient exception
        assertFailsWith<InvalidIngredient> {
            runBlocking {
                createRecipe(AUTHOR_ID, authorUsername, createRecipeInputInfo.copy(ingredients = listOf(invalidIngredient)), recipePictures)
            }
        }
    }
}
