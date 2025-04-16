package epicurius.unit.repository.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.firestore.recipe.models.FirestoreUpdateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import kotlinx.coroutines.runBlocking
import java.util.UUID.randomUUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RecipeRepositoryTest : RepositoryTest() {

    private val publicTestUser = createTestUser(tm)

    @Test
    fun `Create a recipe and then retrieve it successfully`() {
        // given information for a new recipe
        val jdbiRecipeInfo = JdbiCreateRecipeModel(
            name = "Pastel de nata",
            authorId = publicTestUser.id,
            servings = 4,
            preparationTime = 30,
            cuisine = Cuisine.MEDITERRANEAN.ordinal,
            mealType = MealType.DESSERT.ordinal,
            intolerances = listOf(Intolerance.EGG, Intolerance.GLUTEN, Intolerance.DAIRY).map { it.ordinal },
            diets = listOf(Diet.OVO_VEGETARIAN, Diet.LACTO_VEGETARIAN).map { it.ordinal },
            ingredients = listOf(
                Ingredient("Eggs", 4, IngredientUnit.X),
                Ingredient("Sugar", 200, IngredientUnit.G),
                Ingredient("Flour", 100, IngredientUnit.G),
                Ingredient("Milk", 500, IngredientUnit.ML),
                Ingredient("Butter", 50, IngredientUnit.G)
            ),
            picturesNames = listOf("")
        )

        // when creating the recipe
        val recipeId = jdbiCreateRecipe(jdbiRecipeInfo)

        // then the recipe is created successfully
        val jdbiRecipe = getJdbiRecipe(recipeId)
        assertNotNull(jdbiRecipe)
        assertEquals(jdbiRecipeInfo.name, jdbiRecipe.name)
        assertEquals(jdbiRecipeInfo.authorId, jdbiRecipe.authorId)
        assertEquals(publicTestUser.name, jdbiRecipe.authorUsername)
        assertEquals(jdbiRecipeInfo.servings, jdbiRecipe.servings)
        assertEquals(jdbiRecipeInfo.preparationTime, jdbiRecipe.preparationTime)
        assertEquals(jdbiRecipeInfo.cuisine, jdbiRecipe.cuisine.ordinal)
        assertEquals(jdbiRecipeInfo.mealType, jdbiRecipe.mealType.ordinal)
        assertEquals(jdbiRecipeInfo.intolerances, jdbiRecipe.intolerances.map { it.ordinal })
        assertEquals(jdbiRecipeInfo.diets, jdbiRecipe.diets.map { it.ordinal })
        assertEquals(jdbiRecipeInfo.ingredients, jdbiRecipe.ingredients)
        assertEquals(jdbiRecipeInfo.calories, jdbiRecipe.calories)
        assertEquals(jdbiRecipeInfo.protein, jdbiRecipe.protein)
        assertEquals(jdbiRecipeInfo.fat, jdbiRecipe.fat)
        assertEquals(jdbiRecipeInfo.carbs, jdbiRecipe.carbs)
        assertEquals(jdbiRecipeInfo.picturesNames, jdbiRecipe.picturesNames)

        // when creating the recipe in Firestore
        val firestoreRecipeInfo = FirestoreRecipeModel(
            recipeId,
            "A delicious Portuguese dessert",
            Instructions(
                mapOf(
                    "1" to "Preheat the oven to 200°C.",
                    "2" to "In a bowl, mix the eggs, sugar, flour, and milk.",
                    "3" to "Pour the mixture into pastry shells.",
                    "4" to "Bake for 20 minutes or until golden brown.",
                    "5" to "Let cool before serving."
                )
            )
        )

        firestoreCreateRecipe(firestoreRecipeInfo)

        // then the recipe is created successfully
        val firestoreRecipe = runBlocking { getFirestoreRecipe(recipeId) }
        assertNotNull(firestoreRecipe)
        assertEquals(firestoreRecipeInfo.description, firestoreRecipe.description)
        assertEquals(firestoreRecipeInfo.instructions, firestoreRecipe.instructions)
    }

    @Test
    fun `Create a recipe and then delete it successfully`() {
        // given a recipe
        val recipe = createTestRecipe(tm, fs, publicTestUser)

        // when deleting the recipe
        deleteJdbiRecipe(recipe.id)
        deleteFirestoreRecipe(recipe.id)

        // then the recipe is deleted successfully
        val jdbiRecipe = getJdbiRecipe(recipe.id)
        val firestoreRecipe = runBlocking { getFirestoreRecipe(recipe.id) }
        assertNull(jdbiRecipe)
        assertNull(firestoreRecipe)
    }

    @Test
    fun `Create recipe and search by name`() {
        // given a user and a recipe
        val author = createTestUser(tm)
        val recipe = createTestRecipe(tm, fs, author)

        // when searching for the recipe by name
        val searchName = SearchRecipesModel(name = recipe.name)
        val nameResults = searchJdbiRecipes(publicTestUser.id, searchName)

        // then the recipe is found
        assertEquals(1, nameResults.size)
        assertEquals(recipe.name, nameResults[0].name)

        // when searching for the recipe by a different name
        val searchDifferentName = SearchRecipesModel(name = "Nonexistent Recipe")
        val differentNameResults = searchJdbiRecipes(publicTestUser.id, searchDifferentName)

        // then no recipes are found
        assertEquals(0, differentNameResults.size)
    }

    @Test
    fun `Create recipe and search by multiple filters`() {
        // given a user
        val author = createTestUser(tm)

        // given recipe info
        val jdbiRecipeInfo = JdbiCreateRecipeModel(
            name = "Buffalo Cauliflower Wings",
            authorId = author.id,
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

        // when creating the recipe
        val recipeId = jdbiCreateRecipe(jdbiRecipeInfo)

        // and the recipe is retrieved
        val recipe = getJdbiRecipe(recipeId)
        assertNotNull(recipe)

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
            val results = searchJdbiRecipes(publicTestUser.id, searchModel)

            // then the recipe is found
            assertEquals(1, results.size)
            assertEquals(recipe.name, results[0].name)
            assertEquals(recipe.cuisine, results[0].cuisine)
            assertEquals(recipe.mealType, results[0].mealType)
            assertEquals(recipe.preparationTime, results[0].preparationTime)
            assertEquals(recipe.servings, results[0].servings)
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
            val results = searchJdbiRecipes(publicTestUser.id, searchModel)

            // then no recipes are found
            assertEquals(0, results.size)
        }
    }

    @Test
    fun `Update a recipe and then retrieve it successfully`() {
        // given a recipe and new information for updating it
        val recipe = createTestRecipe(tm, fs, publicTestUser)

        // when updating the recipe
        val jdbiUpdateRecipeInfo = JdbiUpdateRecipeModel(
            recipe.id,
            "21312312",
            1,
            15,
            Cuisine.ASIAN.ordinal,
            MealType.SOUP.ordinal,
            listOf(Intolerance.PEANUT.ordinal),
            listOf(Diet.KETOGENIC.ordinal),
            listOf(
                Ingredient("Abc", 23, IngredientUnit.TSP),
                Ingredient("12asd", 1231, IngredientUnit.COFFEE_CUP)
            ),
            1,
            9,
            0,
            4,
            listOf("123")
        )
        val updatedJdbiRecipe = updateJdbiRecipe(jdbiUpdateRecipeInfo)

        // then the recipe is updated successfully
        assertEquals(jdbiUpdateRecipeInfo.name, updatedJdbiRecipe.name)
        assertEquals(publicTestUser.name, updatedJdbiRecipe.authorUsername)
        assertEquals(jdbiUpdateRecipeInfo.servings, updatedJdbiRecipe.servings)
        assertEquals(jdbiUpdateRecipeInfo.preparationTime, updatedJdbiRecipe.preparationTime)
        assertEquals(jdbiUpdateRecipeInfo.cuisine, updatedJdbiRecipe.cuisine.ordinal)
        assertEquals(jdbiUpdateRecipeInfo.mealType, updatedJdbiRecipe.mealType.ordinal)
        assertEquals(jdbiUpdateRecipeInfo.intolerances, updatedJdbiRecipe.intolerances.map { it.ordinal })
        assertEquals(jdbiUpdateRecipeInfo.diets, updatedJdbiRecipe.diets.map { it.ordinal })
        assertEquals(jdbiUpdateRecipeInfo.ingredients, updatedJdbiRecipe.ingredients)
        assertEquals(jdbiUpdateRecipeInfo.calories, updatedJdbiRecipe.calories)
        assertEquals(jdbiUpdateRecipeInfo.protein, updatedJdbiRecipe.protein)
        assertEquals(jdbiUpdateRecipeInfo.fat, updatedJdbiRecipe.fat)
        assertEquals(jdbiUpdateRecipeInfo.carbs, updatedJdbiRecipe.carbs)
        assertEquals(jdbiUpdateRecipeInfo.picturesNames, updatedJdbiRecipe.picturesNames)

        // when updating the recipe in Firestore
        val firestoreRecipeInfo = FirestoreUpdateRecipeModel(
            recipe.id,
            randomUUID().toString(),
            Instructions(mapOf("1" to randomUUID().toString(), "2" to randomUUID().toString()))
        )

        val updatedFirestoreRecipe = runBlocking { updateFirestoreRecipe(firestoreRecipeInfo) }

        // then the recipe is updated successfully
        assertEquals(firestoreRecipeInfo.description, updatedFirestoreRecipe.description)
        assertEquals(firestoreRecipeInfo.instructions, updatedFirestoreRecipe.instructions)
    }
}
