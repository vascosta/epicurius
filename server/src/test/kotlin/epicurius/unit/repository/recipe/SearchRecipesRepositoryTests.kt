package epicurius.unit.repository.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Cuisine.Companion.fromInt
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.MealType.Companion.fromInt
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchRecipesRepositoryTests : RecipeRepositoryTest() {

    @Test
    fun `Should search a recipe by name`() {
        // given a user and a recipe (testUser, testRecipe)

        // when searching for the recipe by name
        val searchName = SearchRecipesModel(name = testRecipe.name)
        val nameResults = searchRecipes(testUser.id, searchName)

        // then the recipe is found
        assertEquals(1, nameResults.size)
        assertEquals(testRecipe.name, nameResults[0].name)

        // when searching for the recipe by a different name
        val searchDifferentName = SearchRecipesModel(name = "Nonexistent Recipe")
        val differentNameResults = searchRecipes(testUser.id, searchDifferentName)

        // then no recipes are found
        assertEquals(0, differentNameResults.size)
    }

    @Test
    fun `Should search a recipe by multiple filters`() {
        // given a user (testUser) and a recipe
        val jdbiRecipeInfo = JdbiCreateRecipeModel(
            name = "Buffalo Cauliflower Wings",
            authorId = testUser.id,
            servings = 4,
            preparationTime = 30,
            cuisine = Cuisine.ASIAN.ordinal,
            mealType = MealType.APPETIZER.ordinal,
            intolerances = listOf(Intolerance.PEANUT.ordinal),
            diets = listOf(Diet.VEGAN.ordinal, Diet.PALEO.ordinal),
            ingredients = listOf(
                Ingredient("Cauliflower", 1, IngredientUnit.X),
                Ingredient("Buffalo Sauce", 100, IngredientUnit.ML),
                Ingredient("Flour", 200, IngredientUnit.G),
                Ingredient("Spices", 10, IngredientUnit.G)
            ),
            calories = 200,
            protein = 5,
            fat = 10,
            carbs = 30,
            picturesNames = listOf("")
        )
        jdbiCreateRecipe(jdbiRecipeInfo)

        // given multiple filters to test
        val filtersToTest = listOf(
            SearchRecipesModel(cuisine = Cuisine.ASIAN.ordinal),
            SearchRecipesModel(mealType = MealType.APPETIZER.ordinal),
            SearchRecipesModel(intolerances = listOf(Intolerance.PEANUT.ordinal)),
            SearchRecipesModel(diets = listOf(Diet.PALEO.ordinal)),
            SearchRecipesModel(minCalories = 100, maxCalories = 500),
            SearchRecipesModel(minProtein = 5, maxProtein = 10),
            SearchRecipesModel(minFat = 5, maxFat = 20),
            SearchRecipesModel(minCarbs = 20, maxCarbs = 100),
        )

        for (searchModel in filtersToTest) {
            // when searching for the recipe with the current filter
            val results = searchRecipes(testAuthor.id, searchModel)

            // then the recipe is found
            assertEquals(1, results.size)
            assertEquals(jdbiRecipeInfo.name, results[0].name)
            assertEquals(Cuisine.fromInt(jdbiRecipeInfo.cuisine), results[0].cuisine)
            assertEquals(MealType.fromInt(jdbiRecipeInfo.mealType), results[0].mealType)
            assertEquals(jdbiRecipeInfo.preparationTime, results[0].preparationTime)
            assertEquals(jdbiRecipeInfo.servings, results[0].servings)
        }

        // given multiple filters that do not match the recipe
        val nonMatchingFilters = listOf(
            SearchRecipesModel(cuisine = Cuisine.AMERICAN.ordinal),
            SearchRecipesModel(mealType = MealType.BEVERAGE.ordinal),
            SearchRecipesModel(intolerances = listOf(Intolerance.SULFITE.ordinal)),
            SearchRecipesModel(diets = listOf(Diet.LOW_FODMAP.ordinal)),
            SearchRecipesModel(minCalories = 1000, maxCalories = 2000),
            SearchRecipesModel(minProtein = 100, maxProtein = 200),
            SearchRecipesModel(minFat = 50, maxFat = 100),
            SearchRecipesModel(minCarbs = 200, maxCarbs = 500),
        )

        for (searchModel in nonMatchingFilters) {
            // when searching for the recipe with the current filter
            val results = searchRecipes(testAuthor.id, searchModel)

            // then no recipes are found
            assertEquals(0, results.size)
        }
    }
}
