package epicurius.unit.repository.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.domain.user.FollowingStatus
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
        val testRecipe = createTestRecipe(tm, testAuthor.user)

        val jdbiRecipeInfo1 = JdbiCreateRecipeModel(
            name = "Spaghetti Bolognese",
            authorId = testAuthor.user.id,
            description = "Spaghetti Bologne",
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
            instructions = Instructions(
                mapOf(
                    "1" to "Cook spaghetti according to package instructions.",
                    "2" to "In a pan, cook ground beef until browned.",
                    "3" to "Add chopped onion and cook until translucent.",
                    "4" to "Stir in tomato sauce and simmer for 10 minutes.",
                    "5" to "Serve sauce over spaghetti."
                )
            ),
            picturesNames = listOf("")
        )

        val jdbiRecipeInfo2 = JdbiCreateRecipeModel(
            name = "Buffalo Cauliflower Wings",
            authorId = testAuthor.user.id,
            description = "Spicy and crispy cauliflower wings",
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
            instructions = Instructions(
                mapOf(
                    "1" to "Preheat oven to 200°C (400°F).",
                    "2" to "Cut cauliflower into bite-sized pieces.",
                    "3" to "Mix flour and spices, then coat cauliflower pieces.",
                    "4" to "Bake for 20 minutes, then toss in buffalo sauce and bake for another 10 minutes."
                )
            ),
            picturesNames = listOf("")
        )

        val jdbiRecipeInfo3 = JdbiCreateRecipeModel(
            name = "Burrito",
            authorId = testAuthor.user.id,
            description = "A delicious burrito with beans, rice, and guacamole",
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
            instructions = Instructions(
                mapOf(
                    "1" to "Warm the tortilla in a pan.",
                    "2" to "Spread beans and rice on the tortilla.",
                    "3" to "Add guacamole on top.",
                    "4" to "Roll the tortilla tightly and serve."
                )
            ),
            picturesNames = listOf("")
        )

        val jdbiRecipeInfo4 = JdbiCreateRecipeModel(
            name = "Chicken Curry",
            authorId = testUserPrivate.user.id,
            description = "A spicy and creamy chicken curry",
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
            instructions = Instructions(
                mapOf(
                    "1" to "Heat oil in a pan and add chicken pieces.",
                    "2" to "Cook until browned, then add curry powder.",
                    "3" to "Pour in coconut milk and simmer for 30 minutes.",
                    "4" to "Serve with cooked rice."
                )
            ),
            picturesNames = listOf("")
        )

        val jdbiRecipeInfo5 = JdbiCreateRecipeModel(
            name = "Vegetable Stir Fry",
            authorId = testUserPublic.user.id,
            description = "A quick and healthy vegetable stir fry",
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
            instructions = Instructions(
                mapOf(
                    "1" to "Heat oil in a wok.",
                    "2" to "Add chopped vegetables and stir fry for 5 minutes.",
                    "3" to "Pour in soy sauce and cook for another 2 minutes.",
                    "4" to "Serve hot."
                )
            ),
            picturesNames = listOf("")
        )

        fun jdbiCreateRecipe(recipeInfo: JdbiCreateRecipeModel) = tm.run { it.recipeRepository.createRecipe(recipeInfo) }

        fun getJdbiRecipeById(recipeId: Int) = tm.run { it.recipeRepository.getRecipeById(recipeId) }

        fun getUserRecipes(userId: Int, lastRecipeId: Int?, limit: Int) =
            tm.run { it.recipeRepository.getUserRecipes(userId, lastRecipeId, limit) }

        fun getRandomRecipesFromPublicUsers(
            userId: Int,
            mealType: MealType,
            intolerances: List<Intolerance>,
            diets: List<Diet>,
            limit: Int
        ) =
            tm.run { it.recipeRepository.getRandomRecipesFromPublicUsers(userId, mealType, intolerances, diets, limit) }

        fun searchRecipes(
            userId: Int,
            form: SearchRecipesModel,
            lastRecipeId: Int?,
            limit: Int
        ) =
            tm.run { it.recipeRepository.searchRecipes(userId, form, lastRecipeId, limit) }

        fun followUser(userId: Int, followedUserId: Int) =
            tm.run { it.userRepository.follow(userId, followedUserId, FollowingStatus.ACCEPTED.ordinal) }

        fun updateJdbiRecipe(recipeInfo: JdbiUpdateRecipeModel) =
            tm.run { it.recipeRepository.updateRecipe(recipeInfo) }

        fun deleteJdbiRecipe(recipeId: Int) = tm.run { it.recipeRepository.deleteRecipe(recipeId) }
    }
}
