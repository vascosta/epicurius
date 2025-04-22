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

        // when searching for a nonexistent recipe name
        val searchDifferentName = SearchRecipesModel(name = "Nonexistent Recipe")
        val differentNameResults = searchRecipes(testUser.id, searchDifferentName)

        // then no recipes are found
        assertEquals(0, differentNameResults.size)
    }

    @Test
    fun `Should search for a recipe by multiple filters without ingredients`() {
        // given a user (testUser) and a recipe
        val jdbiRecipeInfo = JdbiCreateRecipeModel(
            name = "Buffalo Cauliflower Wings",
            authorId = testAuthor.id,
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
        val filtersToTest =
            SearchRecipesModel(
                cuisine = Cuisine.ASIAN.ordinal,
                mealType = MealType.APPETIZER.ordinal,
                intolerances = listOf(Intolerance.PEANUT.ordinal),
                diets = listOf(Diet.PALEO.ordinal),
                minCalories = 100,
                maxCalories = 500,
                minProtein = 5,
                maxProtein = 10,
                minFat = 5,
                maxFat = 20,
                minCarbs = 20,
                maxCarbs = 100
            )

        // when searching for the recipe with the current filter
        val recipeList = searchRecipes(testUser.id, filtersToTest)

        // then the recipe is found
        assertEquals(1, recipeList.size)
        assertEquals(jdbiRecipeInfo.name, recipeList[0].name)
        assertEquals(Cuisine.fromInt(jdbiRecipeInfo.cuisine), recipeList[0].cuisine)
        assertEquals(MealType.fromInt(jdbiRecipeInfo.mealType), recipeList[0].mealType)
        assertEquals(jdbiRecipeInfo.preparationTime, recipeList[0].preparationTime)
        assertEquals(jdbiRecipeInfo.servings, recipeList[0].servings)

        // given multiple filters that do not match the recipe
        val nonMatchingFilters =
            SearchRecipesModel(
                cuisine = Cuisine.AMERICAN.ordinal,
                mealType = MealType.BEVERAGE.ordinal,
                intolerances = listOf(Intolerance.SULFITE.ordinal),
                diets = listOf(Diet.LOW_FODMAP.ordinal),
                minCalories = 1000,
                maxCalories = 2000,
                minProtein = 100,
                maxProtein = 200,
                minFat = 50,
                maxFat = 100,
                minCarbs = 200,
                maxCarbs = 500
            )

        // when searching for the recipe with the current filter
        val emptyList = searchRecipes(testUser.id, nonMatchingFilters)

        // then no recipes are found
        assertEquals(0, emptyList.size)
    }

    @Test
    fun `Should search for a recipe by multiple filters with ingredients`() {
        // given a user (testUser) and a recipe
        val jdbiRecipeInfo = JdbiCreateRecipeModel(
            name = "Burrito",
            authorId = testAuthor.id,
            servings = 2,
            preparationTime = 20,
            cuisine = Cuisine.MEXICAN.ordinal,
            mealType = MealType.SIDE_DISH.ordinal,
            intolerances = listOf(Intolerance.SESAME, Intolerance.WHEAT).map { it.ordinal },
            diets = listOf(Diet.VEGAN, Diet.VEGETARIAN).map { it.ordinal },
            ingredients = listOf(
                Ingredient("Tortilla", 1, IngredientUnit.X),
                Ingredient("Beans", 100, IngredientUnit.G),
                Ingredient("Rice", 200, IngredientUnit.G),
                Ingredient("Guacamole", 50, IngredientUnit.G)
            ),
            calories = 300,
            protein = 10,
            fat = 15,
            carbs = 40,
            picturesNames = listOf("")
        )
        jdbiCreateRecipe(jdbiRecipeInfo)

        // given multiple filters to test
        val filtersToTest =
            SearchRecipesModel(
                name = "Burrito",
                cuisine = Cuisine.MEXICAN.ordinal,
                mealType = MealType.SIDE_DISH.ordinal,
                intolerances = listOf(Intolerance.SESAME.ordinal, Intolerance.WHEAT.ordinal),
                diets = listOf(Diet.VEGAN.ordinal, Diet.VEGETARIAN.ordinal),
                ingredients = listOf("Tortilla", "Beans"),
                minCalories = 200,
                maxCalories = 500,
                minProtein = 5,
                maxProtein = 20,
                minFat = 10,
                maxFat = 30,
                minCarbs = 20,
                maxCarbs = 50
            )

        // when searching for the recipe with the current filter
        val recipeList = searchRecipes(testUser.id, filtersToTest)

        // when searching for the recipe by ingredients
        val ingredientsResults = filtersToTest.ingredients?.let { searchRecipesByIngredients(testUser.id, it) }

        // when intersecting the two results
        val intersectedResults = recipeList.intersect((ingredientsResults?.toSet() ?: emptySet()).toSet()).toList()

        // then the recipe is found
        assertEquals(1, intersectedResults.size)
        assertEquals(jdbiRecipeInfo.name, intersectedResults[0].name)
        assertEquals(Cuisine.fromInt(jdbiRecipeInfo.cuisine), intersectedResults[0].cuisine)
        assertEquals(MealType.fromInt(jdbiRecipeInfo.mealType), intersectedResults[0].mealType)
        assertEquals(jdbiRecipeInfo.preparationTime, intersectedResults[0].preparationTime)
        assertEquals(jdbiRecipeInfo.servings, intersectedResults[0].servings)
        assertEquals(jdbiRecipeInfo.picturesNames, intersectedResults[0].pictures)
    }
}
