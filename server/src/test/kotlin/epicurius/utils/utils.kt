package epicurius.utils

import epicurius.EpicuriusTest.Companion.usersDomain
import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Cuisine.Companion.fromInt
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.MealType.Companion.fromInt
import epicurius.domain.recipe.Recipe
import epicurius.domain.user.UpdateUserModel
import epicurius.domain.user.User
import epicurius.domain.user.UserDomain.Companion.MAX_PASSWORD_LENGTH
import epicurius.domain.user.UserDomain.Companion.MAX_USERNAME_LENGTH
import epicurius.repository.firestore.FirestoreManager
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import epicurius.repository.transaction.TransactionManager
import java.util.UUID

fun createTestUser(tm: TransactionManager, privacy: Boolean = false): User {
    val username = generateRandomUsername()
    val email = generateEmail(username)
    val country = "PT"
    val password = generateSecurePassword()
    val passwordHash = usersDomain.encodePassword(password)

    tm.run { it.userRepository.createUser(username, email, country, passwordHash) }
    if (privacy) {
        tm.run { it.userRepository.updateUser(username, UpdateUserModel(privacy = true)) }
    }

    return tm.run { it.userRepository.getUser(username) } ?: throw Exception("User not created")
}

fun createTestRecipe(tm: TransactionManager, fs: FirestoreManager, user: User): Recipe {
    val jdbiRecipeInfo = JdbiCreateRecipeModel(
        name = "Pastel de nata",
        authorId = user.id,
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

    val recipeId = tm.run { it.recipeRepository.createRecipe(jdbiRecipeInfo) }

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

    fs.recipeRepository.createRecipe(firestoreRecipeInfo)

    return Recipe(
        recipeId,
        jdbiRecipeInfo.name,
        user.username,
        jdbiRecipeInfo.date,
        firestoreRecipeInfo.description,
        jdbiRecipeInfo.servings,
        jdbiRecipeInfo.preparationTime,
        Cuisine.fromInt(jdbiRecipeInfo.cuisine),
        MealType.fromInt(jdbiRecipeInfo.mealType),
        jdbiRecipeInfo.intolerances.map { Intolerance.fromInt(it) },
        jdbiRecipeInfo.diets.map { Diet.fromInt(it) },
        jdbiRecipeInfo.ingredients,
        jdbiRecipeInfo.calories,
        jdbiRecipeInfo.protein,
        jdbiRecipeInfo.fat,
        jdbiRecipeInfo.carbs,
        firestoreRecipeInfo.instructions,
        listOf(ByteArray(1))
    )
}

fun generateRandomUsername() = "test${Math.random()}".replace(".", "").take(MAX_USERNAME_LENGTH)
fun generateEmail(username: String) = "$username@email.com"
fun generateSecurePassword() = ("P" + UUID.randomUUID().toString()).take(MAX_PASSWORD_LENGTH)
