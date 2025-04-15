package epicurius.unit.services.recipe

import epicurius.domain.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.exceptions.InvalidNumberOfRecipePictures
import org.mockito.kotlin.argThat
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
        // given information for a new recipe (jdbiCreateRecipeInfo, firestoreRecipeInfo)

        // mock
        whenever(pictureDomainMock.generatePictureName()).thenReturn(recipePicturesNames.first())
        whenever(jdbiRecipeRepositoryMock.createRecipe(
            argThat { model ->
                model.name == createRecipeInfo.name &&
                model.authorId == AUTHOR_ID &&
                model.servings == createRecipeInfo.servings &&
                model.preparationTime == createRecipeInfo.preparationTime &&
                model.cuisine == createRecipeInfo.cuisine.ordinal &&
                model.mealType == createRecipeInfo.mealType.ordinal &&
                model.intolerances == createRecipeInfo.intolerances.map { it.ordinal } &&
                model.diets == createRecipeInfo.diets.map { it.ordinal } &&
                model.ingredients == createRecipeInfo.ingredients &&
                model.calories == createRecipeInfo.calories &&
                model.protein == createRecipeInfo.protein &&
                model.fat == createRecipeInfo.fat &&
                model.carbs == createRecipeInfo.carbs &&
                model.picturesNames == recipePicturesNames

            })
        ).thenReturn(RECIPE_ID)

        // when creating the recipe
        val recipe = recipeService.createRecipe(AUTHOR_ID, authorName, createRecipeInfo, recipePictures)
        verify(firestoreRecipeRepositoryMock).createRecipe(firestoreRecipeInfo)
        verify(cloudStoragePictureRepositoryMock).updatePicture(recipePicturesNames.first(), recipePictures.first(), RECIPES_FOLDER)

        // then the recipe is created successfully
        assertEquals(RECIPE_ID, recipe.id)
        assertEquals(createRecipeInfo.name, recipe.name)
        assertEquals(authorName, recipe.authorUsername)
        assertEquals(createRecipeInfo.description, recipe.description)
        assertEquals(createRecipeInfo.servings, recipe.servings)
        assertEquals(createRecipeInfo.preparationTime, recipe.preparationTime)
        assertEquals(createRecipeInfo.cuisine, recipe.cuisine)
        assertEquals(createRecipeInfo.mealType, recipe.mealType)
        assertEquals(createRecipeInfo.intolerances, recipe.intolerances)
        assertEquals(createRecipeInfo.diets, recipe.diets)
        assertEquals(createRecipeInfo.ingredients, recipe.ingredients)
        assertEquals(createRecipeInfo.calories, recipe.calories)
        assertEquals(createRecipeInfo.protein, recipe.protein)
        assertEquals(createRecipeInfo.fat, recipe.fat)
        assertEquals(createRecipeInfo.carbs, recipe.carbs)
        assertEquals(createRecipeInfo.instructions, recipe.instructions)
        assertContentEquals(recipePictures.map { it.bytes }, recipe.pictures)
    }

    @Test
    fun `Should throw InvalidNumberOfRecipePictures exception when creating a recipe with an invalid number of pictures`() {
        // given an invalid number of pictures
        val invalidList = emptyList<MultipartFile>()

        // when creating the recipe with invalid number of pictures
        val exception = assertFailsWith<InvalidNumberOfRecipePictures> {
            recipeService.createRecipe(AUTHOR_ID, authorName, createRecipeInfo, invalidList)
        }

        // then an exception is thrown
        assertEquals(InvalidNumberOfRecipePictures().message, exception.message)
    }
}