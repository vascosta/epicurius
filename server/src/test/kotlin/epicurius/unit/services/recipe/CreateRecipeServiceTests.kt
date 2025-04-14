package epicurius.unit.services.recipe

import epicurius.domain.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.exceptions.InvalidNumberOfRecipePictures
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.multipart.MultipartFile
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateRecipeServiceTests: RecipeServiceTest() {

    @Test
    fun `Should create a recipe successfully`() {
        // given information for a new recipe (in companion object)
        // mocks
        whenever(pictureDomainMock.generatePictureName()).thenReturn(recipePicturesNames.first())
        whenever(jdbiRecipeRepositoryMock.createRecipe(jdbiCreateRecipeModel)).thenReturn(RECIPE_ID)

        // when creating the recipe
        val recipe = recipeService.createRecipe(AUTHOR_ID, authorName, recipeInfo, recipePictures)
        verify(firestoreRecipeRepositoryMock).createRecipe(firestoreRecipeModel)
        verify(cloudStoragePictureRepositoryMock).updatePicture(recipePicturesNames.first(), recipePictures.first(), RECIPES_FOLDER)

        // then the recipe is created successfully
        assertEquals(RECIPE_ID, recipe.id)
        assertEquals(recipeInfo.name, recipe.name)
        assertEquals(authorName, recipe.authorUsername)
        assertEquals(recipeInfo.description, recipe.description)
        assertEquals(recipeInfo.servings, recipe.servings)
        assertEquals(recipeInfo.preparationTime, recipe.preparationTime)
        assertEquals(recipeInfo.cuisine, recipe.cuisine)
        assertEquals(recipeInfo.mealType, recipe.mealType)
        assertEquals(recipeInfo.intolerances, recipe.intolerances)
        assertEquals(recipeInfo.diets, recipe.diets)
        assertEquals(recipeInfo.ingredients, recipe.ingredients)
        assertEquals(recipeInfo.calories, recipe.calories)
        assertEquals(recipeInfo.protein, recipe.protein)
        assertEquals(recipeInfo.fat, recipe.fat)
        assertEquals(recipeInfo.carbs, recipe.carbs)
        assertEquals(recipeInfo.instructions, recipe.instructions)
        assertContentEquals(recipePictures.map { it.bytes }, recipe.pictures)
    }

    @Test
    fun `Should throw InvalidNumberOfRecipePictures exception when creating a recipe with an invalid number of pictures`() {
        // given an invalid number of pictures
        val invalidList = emptyList<MultipartFile>()

        // when creating the recipe with invalid number of pictures
        val exception = assertFailsWith<InvalidNumberOfRecipePictures> {
            recipeService.createRecipe(AUTHOR_ID, authorName, recipeInfo, invalidList)
        }

        // then an exception is thrown
        assertEquals(InvalidNumberOfRecipePictures().message, exception.message)
    }
}