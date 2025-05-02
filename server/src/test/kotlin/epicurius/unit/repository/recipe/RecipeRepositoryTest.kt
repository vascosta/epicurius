package epicurius.unit.repository.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.domain.user.FollowingStatus
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.firestore.recipe.models.FirestoreUpdateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser

open class RecipeRepositoryTest : RepositoryTest() {

    companion object {
        val testUserPublic = createTestUser(tm)
        val testUserPrivate = createTestUser(tm, true)
        val testAuthor = createTestUser(tm)
        val testRecipe = createTestRecipe(tm, fs, testAuthor)

        val jdbiRecipeInfo1 = JdbiCreateRecipeModel(
            name = "Spaghetti Bolognese",
            authorId = testAuthor.id,
            servings = 4,
            preparationTime = 30,
            cuisine = Cuisine.ITALIAN.ordinal,
            mealType = MealType.MAIN_COURSE.ordinal,
            intolerances = listOf(Intolerance.GLUTEN.ordinal),
            diets = listOf(Diet.VEGETARIAN.ordinal),
            ingredients = listOf(
                Ingredient("Spaghetti", 200.0, IngredientUnit.G),
                Ingredient("Ground Beef", 300.0, IngredientUnit.G),
                Ingredient("Tomato Sauce", 150.0, IngredientUnit.ML),
                Ingredient("Onion", 1.0, IngredientUnit.X)
            ),
            calories = 600,
            protein = 25,
            fat = 15,
            carbs = 80,
            picturesNames = listOf("")
        )

        val jdbiRecipeInfo2 = JdbiCreateRecipeModel(
            name = "Buffalo Cauliflower Wings",
            authorId = testAuthor.id,
            servings = 4,
            preparationTime = 30,
            cuisine = Cuisine.ASIAN.ordinal,
            mealType = MealType.APPETIZER.ordinal,
            intolerances = listOf(Intolerance.PEANUT, Intolerance.GLUTEN, Intolerance.DAIRY).map { it.ordinal },
            diets = listOf(Diet.VEGAN.ordinal, Diet.PALEO.ordinal),
            ingredients = listOf(
                Ingredient("Cauliflower", 1.0, IngredientUnit.X),
                Ingredient("Buffalo Sauce", 100.0, IngredientUnit.ML),
                Ingredient("Flour", 200.0, IngredientUnit.G),
                Ingredient("Spices", 10.0, IngredientUnit.G)
            ),
            calories = 200,
            protein = 5,
            fat = 10,
            carbs = 30,
            picturesNames = listOf("")
        )

        val jdbiRecipeInfo3 = JdbiCreateRecipeModel(
            name = "Burrito",
            authorId = testAuthor.id,
            servings = 2,
            preparationTime = 20,
            cuisine = Cuisine.MEXICAN.ordinal,
            mealType = MealType.SIDE_DISH.ordinal,
            intolerances = listOf(Intolerance.SESAME, Intolerance.WHEAT, Intolerance.GLUTEN, Intolerance.DAIRY).map { it.ordinal },
            diets = listOf(Diet.VEGAN, Diet.VEGETARIAN).map { it.ordinal },
            ingredients = listOf(
                Ingredient("Tortilla", 1.0, IngredientUnit.X),
                Ingredient("Beans", 100.0, IngredientUnit.G),
                Ingredient("Rice", 200.0, IngredientUnit.G),
                Ingredient("Guacamole", 50.0, IngredientUnit.G)
            ),
            calories = 300,
            protein = 10,
            fat = 15,
            carbs = 40,
            picturesNames = listOf("")
        )

        val jdbiRecipeInfo4 = JdbiCreateRecipeModel(
            name = "Chicken Curry",
            authorId = testUserPrivate.id,
            servings = 4,
            preparationTime = 45,
            cuisine = Cuisine.INDIAN.ordinal,
            mealType = MealType.MAIN_COURSE.ordinal,
            intolerances = listOf(Intolerance.GLUTEN.ordinal, Intolerance.DAIRY.ordinal),
            diets = listOf(Diet.VEGETARIAN.ordinal),
            ingredients = listOf(
                Ingredient("Chicken", 500.0, IngredientUnit.G),
                Ingredient("Curry Powder", 20.0, IngredientUnit.G),
                Ingredient("Coconut Milk", 400.0, IngredientUnit.ML),
                Ingredient("Rice", 200.0, IngredientUnit.G)
            ),
            calories = 700,
            protein = 40,
            fat = 25,
            carbs = 60,
            picturesNames = listOf("")
        )

        val jdbiRecipeInfo5 = JdbiCreateRecipeModel(
            name = "Vegetable Stir Fry",
            authorId = testUserPublic.id,
            servings = 4,
            preparationTime = 20,
            cuisine = Cuisine.CHINESE.ordinal,
            mealType = MealType.SIDE_DISH.ordinal,
            intolerances = listOf(Intolerance.GLUTEN.ordinal),
            diets = listOf(Diet.VEGAN.ordinal, Diet.PALEO.ordinal),
            ingredients = listOf(
                Ingredient("Broccoli", 200.0, IngredientUnit.G),
                Ingredient("Carrots", 100.0, IngredientUnit.G),
                Ingredient("Bell Pepper", 1.0, IngredientUnit.X),
                Ingredient("Soy Sauce", 50.0, IngredientUnit.ML)
            ),
            calories = 150,
            protein = 5,
            fat = 5,
            carbs = 20,
            picturesNames = listOf("")
        )

        fun jdbiCreateRecipe(recipeInfo: JdbiCreateRecipeModel) = tm.run { it.recipeRepository.createRecipe(recipeInfo) }

        fun firestoreCreateRecipe(recipeInfo: FirestoreRecipeModel) {
            fs.recipeRepository.createRecipe(recipeInfo)
        }

        fun getJdbiRecipe(recipeId: Int) = tm.run { it.recipeRepository.getRecipe(recipeId) }

        suspend fun getFirestoreRecipe(recipeId: Int) = fs.recipeRepository.getRecipe(recipeId)

        fun getRandomRecipesFromPublicUsers(mealType: MealType, intolerances: List<Intolerance>, diets: List<Diet>, limit: Int) =
            tm.run { it.recipeRepository.getRandomRecipesFromPublicUsers(mealType, intolerances, diets, limit) }

        fun searchRecipes(userId: Int, form: SearchRecipesModel, pagingParams: PagingParams) =
            tm.run { it.recipeRepository.searchRecipes(userId, form, pagingParams) }

        fun followUser(userId: Int, followedUserId: Int) =
            tm.run { it.userRepository.follow(userId, followedUserId, FollowingStatus.ACCEPTED.ordinal) }

        fun updateJdbiRecipe(recipeInfo: JdbiUpdateRecipeModel) =
            tm.run { it.recipeRepository.updateRecipe(recipeInfo) }

        suspend fun updateFirestoreRecipe(recipeInfo: FirestoreUpdateRecipeModel) =
            fs.recipeRepository.updateRecipe(recipeInfo)

        fun deleteJdbiRecipe(recipeId: Int) = tm.run { it.recipeRepository.deleteRecipe(recipeId) }
        fun deleteFirestoreRecipe(recipeId: Int) = fs.recipeRepository.deleteRecipe(recipeId)
    }
}
