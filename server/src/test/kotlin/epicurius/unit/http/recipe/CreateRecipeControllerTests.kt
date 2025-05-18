package epicurius.unit.http.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.InvalidIngredient
import epicurius.domain.exceptions.InvalidNumberOfRecipePictures
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.Recipe
import epicurius.http.controllers.recipe.models.input.CreateRecipeInputModel
import epicurius.http.controllers.recipe.models.output.CreateRecipeOutputModel
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateRecipeControllerTests : RecipeControllerTest() {

    private val createRecipeInfo = CreateRecipeInputModel(
        "Pastel de nata",
        "A delicious Portuguese dessert",
        4,
        30,
        Cuisine.MEDITERRANEAN,
        MealType.DESSERT,
        setOf(Intolerance.EGG, Intolerance.GLUTEN, Intolerance.DAIRY),
        setOf(Diet.OVO_VEGETARIAN, Diet.LACTO_VEGETARIAN),
        listOf(
            Ingredient("Eggs", 4.0, IngredientUnit.X),
            Ingredient("Sugar", 200.0, IngredientUnit.G),
            Ingredient("Flour", 100.0, IngredientUnit.G),
            Ingredient("Milk", 500.0, IngredientUnit.ML),
            Ingredient("Butter", 50.0, IngredientUnit.G)
        ),
        instructions = Instructions(
            mapOf(
                "1" to "Preheat the oven to 200°C.",
                "2" to "In a bowl, mix the eggs, sugar, flour, and milk.",
                "3" to "Pour the mixture into pastry shells.",
                "4" to "Bake for 20 minutes or until golden brown.",
                "5" to "Let cool before serving."
            )
        )
    )

    @Test
    fun `Should create a recipe successfully`() {
        // given information for a new recipe (createRecipeInfo, recipePictures)

        // mock
        val recipeMock = Recipe(
            RECIPE_ID,
            createRecipeInfo.name,
            testAuthenticatedUser.user.name,
            LocalDate.now(),
            createRecipeInfo.description,
            createRecipeInfo.servings,
            createRecipeInfo.preparationTime,
            createRecipeInfo.cuisine,
            createRecipeInfo.mealType,
            createRecipeInfo.intolerances.toList(),
            createRecipeInfo.diets.toList(),
            createRecipeInfo.ingredients,
            createRecipeInfo.calories,
            createRecipeInfo.protein,
            createRecipeInfo.fat,
            createRecipeInfo.carbs,
            createRecipeInfo.instructions,
            recipePictures.map { it.bytes }
        )
        whenever(
            runBlocking {
                recipeServiceMock
                    .createRecipe(
                        testAuthenticatedUser.user.id,
                        testAuthenticatedUser.user.name,
                        createRecipeInfo,
                        recipePictures.toSet()
                    )
            }

        ).thenReturn(recipeMock)

        // when creating the recipe
        val response = runBlocking {
            createRecipe(testAuthenticatedUser, objectMapper.writeValueAsString(createRecipeInfo), recipePictures)
        }

        // then the recipe is created successfully
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(CreateRecipeOutputModel(recipeMock), response.body)
    }

    @Test
    fun `Should throw InvalidNumberOfRecipePictures exception when creating a recipe with an invalid number of pictures`() {
        // given information for a new recipe (createRecipeInfo, recipePictures) and an invalid number of pictures
        val invalidNumberOfRecipePicturesSet = emptyList<MultipartFile>()

        // mock
        whenever(
            runBlocking {
                recipeServiceMock.createRecipe(
                    testAuthenticatedUser.user.id,
                    testAuthenticatedUser.user.name,
                    createRecipeInfo,
                    invalidNumberOfRecipePicturesSet.toSet()
                )
            }

        ).thenThrow(InvalidNumberOfRecipePictures())

        // when creating the recipe
        // then the recipe is not created and throws InvalidNumberOfRecipePictures exception
        assertFailsWith<InvalidNumberOfRecipePictures> {
            runBlocking {
                createRecipe(
                    testAuthenticatedUser, objectMapper.writeValueAsString(createRecipeInfo), invalidNumberOfRecipePicturesSet
                )
            }
        }
    }

    @Test
    fun `Should throw InvalidIngredient exception when creating a recipe with an invalid ingredient`() {
        // given information for a new recipe (createRecipeInfo, recipePictures) and an invalid ingredient
        val invalidIngredient = Ingredient("invalid", 1.0, IngredientUnit.G)

        // mock
        whenever(
            runBlocking {
                recipeServiceMock.createRecipe(
                    testAuthenticatedUser.user.id,
                    testAuthenticatedUser.user.name,
                    createRecipeInfo,
                    recipePictures.toSet()
                )
            }

        ).thenThrow(InvalidIngredient(invalidIngredient.name))

        // when creating the recipe
        // then the recipe is not created and throws InvalidIngredient exception
        assertFailsWith<InvalidIngredient> {
            runBlocking {
                createRecipe(testAuthenticatedUser, objectMapper.writeValueAsString(createRecipeInfo), recipePictures)
            }
        }
    }
}
