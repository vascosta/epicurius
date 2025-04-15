package epicurius.unit.http.recipe

import epicurius.domain.recipe.Recipe
import epicurius.http.recipe.models.output.CreateRecipeOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import java.time.Instant
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals


class CreateRecipeHttpTest: RecipeHttpTest() {

    @Test
    fun `Should create a recipe successfully`() {
        // given information for a new recipe (createRecipeInfo, recipePictures)

        // mock
        val recipeMock = Recipe(
            RECIPE_ID,
            createRecipeInfo.name,
            authenticatedUser.user.name,
            Date.from(Instant.now()),
            createRecipeInfo.description,
            createRecipeInfo.servings,
            createRecipeInfo.preparationTime,
            createRecipeInfo.cuisine,
            createRecipeInfo.mealType,
            createRecipeInfo.intolerances,
            createRecipeInfo.diets,
            createRecipeInfo.ingredients,
            createRecipeInfo.calories,
            createRecipeInfo.protein,
            createRecipeInfo.fat,
            createRecipeInfo.carbs,
            createRecipeInfo.instructions,
            recipePictures.map { it.bytes }
        )
        whenever(recipeServiceMock
            .createRecipe(
                authenticatedUser.user.id,
                authenticatedUser.user.name,
                createRecipeInfo,
                recipePictures)
        ).thenReturn(recipeMock)

        // when creating the recipe
        val response = createRecipe(authenticatedUser, objectMapper.writeValueAsString(createRecipeInfo), recipePictures)

        // then the recipe is created successfully
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(CreateRecipeOutputModel(recipeMock), response.body)

    }
}