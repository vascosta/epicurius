package epicurius.unit.repository.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Cuisine.Companion.fromInt
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.MealType.Companion.fromInt
import epicurius.domain.recipe.SearchRecipesModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchRecipesRepositoryTests : RecipeRepositoryTest() {

    @Test
    fun `Should search a recipe by name`() {
        // given a user and a recipe (testUserPublic, testRecipe) and paging params
        val pagingParams = PagingParams()

        // when searching for the recipe by name
        val searchName = SearchRecipesModel(name = testRecipe.name)
        val nameResults = searchRecipes(testUserPublic.id, searchName, pagingParams)

        // then the recipe is found
        assertEquals(1, nameResults.size)
        assertEquals(testRecipe.name, nameResults[0].name)

        // when searching for a nonexistent recipe name
        val searchDifferentName = SearchRecipesModel(name = "Nonexistent Recipe")
        val differentNameResults = searchRecipes(testUserPublic.id, searchDifferentName, pagingParams)

        // then no recipes are found
        assertEquals(0, differentNameResults.size)
    }

    @Test
    fun `Should search for a recipe according to user's intolerances`() {
        // given a user (testUserPublic) and a recipe
        jdbiCreateRecipe(jdbiRecipeInfo1)

        // given user intolerances that correspond to the recipe
        val filtersToTest = SearchRecipesModel(intolerances = listOf(Intolerance.GLUTEN.ordinal))

        // given paging params
        val pagingParams = PagingParams()

        // when searching for the recipe with the current filter
        val emptyList = searchRecipes(testUserPublic.id, filtersToTest, pagingParams)

        // then the recipe is not found
        assertTrue(emptyList.isEmpty())

        // given user intolerances that do not correspond to the recipe
        val nonMatchingFilters = SearchRecipesModel(intolerances = listOf(Intolerance.DAIRY.ordinal))

        // when searching for the recipe with the current filter
        val recipeList = searchRecipes(testUserPublic.id, nonMatchingFilters, pagingParams)

        // then no recipes are found
        assertEquals(1, recipeList.size)
        assertEquals(jdbiRecipeInfo1.name, recipeList[0].name)
        assertEquals(Cuisine.fromInt(jdbiRecipeInfo1.cuisine), recipeList[0].cuisine)
        assertEquals(MealType.fromInt(jdbiRecipeInfo1.mealType), recipeList[0].mealType)
        assertEquals(jdbiRecipeInfo1.preparationTime, recipeList[0].preparationTime)
        assertEquals(jdbiRecipeInfo1.servings, recipeList[0].servings)
    }

    @Test
    fun `Should search for a recipe by multiple filters without ingredients`() {
        // given a user (testUserPublic) and a recipe
        jdbiCreateRecipe(jdbiRecipeInfo2)

        // given multiple filters to test
        val filtersToTest =
            SearchRecipesModel(
                cuisine = listOf(Cuisine.ASIAN.ordinal),
                mealType = listOf(MealType.APPETIZER.ordinal),
                intolerances = emptyList(),
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

        // given paging params
        val pagingParams = PagingParams()

        // when searching for the recipe with the current filter
        val recipeList = searchRecipes(testUserPublic.id, filtersToTest, pagingParams)

        // then the recipe is found
        assertEquals(1, recipeList.size)
        assertEquals(jdbiRecipeInfo2.name, recipeList[0].name)
        assertEquals(Cuisine.fromInt(jdbiRecipeInfo2.cuisine), recipeList[0].cuisine)
        assertEquals(MealType.fromInt(jdbiRecipeInfo2.mealType), recipeList[0].mealType)
        assertEquals(jdbiRecipeInfo2.preparationTime, recipeList[0].preparationTime)
        assertEquals(jdbiRecipeInfo2.servings, recipeList[0].servings)

        // given multiple filters that do not match the recipe
        val nonMatchingFilters =
            SearchRecipesModel(
                cuisine = listOf(Cuisine.AMERICAN.ordinal),
                mealType = listOf(MealType.BEVERAGE.ordinal),
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
        val emptyList = searchRecipes(testUserPublic.id, nonMatchingFilters, pagingParams)

        // then no recipes are found
        assertEquals(0, emptyList.size)
    }

    @Test
    fun `Should search for a recipe by multiple filters with ingredients`() {
        // given a user (testUserPublic) and a recipe
        jdbiCreateRecipe(jdbiRecipeInfo3)

        // given multiple filters to test
        val filtersToTest =
            SearchRecipesModel(
                name = "Burrito",
                cuisine = listOf(Cuisine.MEXICAN.ordinal),
                mealType = listOf(MealType.SIDE_DISH.ordinal),
                intolerances = emptyList(),
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

        // given paging params
        val pagingParams = PagingParams()

        // when searching for the recipe with the current filter
        val recipeList = searchRecipes(testUserPublic.id, filtersToTest, pagingParams)

        // then the recipe is found
        assertEquals(1, recipeList.size)
        assertEquals(jdbiRecipeInfo3.name, recipeList[0].name)
        assertEquals(Cuisine.fromInt(jdbiRecipeInfo3.cuisine), recipeList[0].cuisine)
        assertEquals(MealType.fromInt(jdbiRecipeInfo3.mealType), recipeList[0].mealType)
        assertEquals(jdbiRecipeInfo3.preparationTime, recipeList[0].preparationTime)
        assertEquals(jdbiRecipeInfo3.servings, recipeList[0].servings)
        assertEquals(jdbiRecipeInfo3.picturesNames, recipeList[0].pictures)
    }

    @Test
    fun `Should search for recipes of public users`(){
        // given a user (testUserPublic) and a recipe
        jdbiCreateRecipe(jdbiRecipeInfo5)

        // given multiple filters to test that match the recipe 5
        val filtersToTest2 =
            SearchRecipesModel(
                cuisine = listOf(Cuisine.CHINESE.ordinal),
                mealType = listOf(MealType.SIDE_DISH.ordinal),
                intolerances = emptyList(),
                diets = listOf(Diet.VEGAN.ordinal, Diet.PALEO.ordinal),
                minCalories = 100,
                maxCalories = 1000,
                minProtein = 5,
                maxProtein = 50,
                minFat = 5,
                maxFat = 50,
                minCarbs = 20,
                maxCarbs = 100
            )

        // given paging params
        val pagingParams = PagingParams()

        // when private user searches for public user recipe
        val recipeList = searchRecipes(testUserPrivate.id, filtersToTest2, pagingParams)

        // then the recipe is found
        assertEquals(1, recipeList.size)
        assertEquals(jdbiRecipeInfo5.name, recipeList[0].name)
        assertEquals(Cuisine.fromInt(jdbiRecipeInfo5.cuisine), recipeList[0].cuisine)
        assertEquals(MealType.fromInt(jdbiRecipeInfo5.mealType), recipeList[0].mealType)
        assertEquals(jdbiRecipeInfo5.preparationTime, recipeList[0].preparationTime)
        assertEquals(jdbiRecipeInfo5.servings, recipeList[0].servings)
    }

    @Test
    fun `Should search for recipes from private users when followed`() {
        // given two users (testUserPublic and testUserPrivate) and a recipe from a private user and another from a public user
        jdbiCreateRecipe(jdbiRecipeInfo4)
        jdbiCreateRecipe(jdbiRecipeInfo5)

        // given multiple filters to test that match the recipe
        val filtersToTest =
            SearchRecipesModel(
                cuisine = listOf(Cuisine.INDIAN.ordinal),
                mealType = listOf(MealType.MAIN_COURSE.ordinal),
                intolerances = emptyList(),
                diets = listOf(Diet.VEGETARIAN.ordinal),
                minCalories = 100,
                maxCalories = 1000,
                minProtein = 5,
                maxProtein = 50,
                minFat = 5,
                maxFat = 50,
                minCarbs = 20,
                maxCarbs = 100
            )

        // given paging params
        val pagingParams = PagingParams()

        // when searching for the recipe with the current filter
        val emptyList = searchRecipes(testUserPublic.id, filtersToTest, pagingParams)

        // then the recipe is found, public user does not follow private user
        assertTrue(emptyList.isEmpty())

        // when public user starts following private user
        followUser(testUserPublic.id, testUserPrivate.id)

        // when searching for the recipe that match the private user recipe
        val recipeList2 = searchRecipes(testUserPublic.id, filtersToTest, pagingParams)

        // then the recipe is found
        assertEquals(1, recipeList2.size)
        assertEquals(jdbiRecipeInfo4.name, recipeList2[0].name)
        assertEquals(Cuisine.fromInt(jdbiRecipeInfo4.cuisine), recipeList2[0].cuisine)
        assertEquals(MealType.fromInt(jdbiRecipeInfo4.mealType), recipeList2[0].mealType)
        assertEquals(jdbiRecipeInfo4.preparationTime, recipeList2[0].preparationTime)
        assertEquals(jdbiRecipeInfo4.servings, recipeList2[0].servings)
    }
}
