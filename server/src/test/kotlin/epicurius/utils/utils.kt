package epicurius.utils

import epicurius.EpicuriusTest.Companion.userDomain
import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.collection.CollectionDomain.Companion.MAX_COLLECTION_NAME_LENGTH
import epicurius.domain.collection.CollectionType
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.Recipe
import epicurius.domain.recipe.RecipeDomain
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.domain.user.UserDomain.Companion.MAX_PASSWORD_LENGTH
import epicurius.domain.user.UserDomain.Companion.MAX_USERNAME_LENGTH
import epicurius.repository.firestore.manager.FirestoreManager
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import epicurius.repository.transaction.TransactionManager
import java.time.LocalDate
import java.util.UUID.randomUUID

fun createTestUser(tm: TransactionManager, privacy: Boolean = false): AuthenticatedUser {
    val username = generateRandomUsername()
    val email = generateEmail(username)
    val country = "PT"
    val password = generateSecurePassword()
    val passwordHash = userDomain.encodePassword(password)

    val userId = tm.run { it.userRepository.createUser(username, email, country, passwordHash) }
    if (privacy) {
        tm.run { it.userRepository.updateUser(userId, JdbiUpdateUserModel(privacy = true)) }
    }

    val token = userDomain.generateTokenValue()
    val tokenHash = userDomain.hashToken(token)
    val lastUsed = LocalDate.now()
    tm.run { it.tokenRepository.createToken(tokenHash, lastUsed, userId) }

    val user = tm.run { it.userRepository.getUser(username) } ?: throw Exception("User not created")
    return AuthenticatedUser(user, token)
}

fun createTestRecipe(tm: TransactionManager, fs: FirestoreManager, user: User): Recipe {
    val jdbiRecipeInfo = JdbiCreateRecipeModel(
        generateRandomRecipeName(),
        authorId = user.id,
        servings = 1,
        preparationTime = 1,
        cuisine = Cuisine.MEDITERRANEAN.ordinal,
        mealType = MealType.DESSERT.ordinal,
        intolerances = listOf(Intolerance.EGG, Intolerance.GLUTEN, Intolerance.DAIRY).map { it.ordinal },
        diets = listOf(Diet.OVO_VEGETARIAN, Diet.LACTO_VEGETARIAN).map { it.ordinal },
        ingredients = generateRandomRecipeIngredients(),
        picturesNames = listOf("test-picture.jpeg")
    )

    val recipeId = tm.run { it.recipeRepository.createRecipe(jdbiRecipeInfo) }

    val firestoreRecipeInfo = FirestoreRecipeModel(
        recipeId,
        generateRandomRecipeDescription(),
        generateRandomRecipeInstructions()
    )

    fs.recipeRepository.createRecipe(firestoreRecipeInfo)

    return Recipe(
        recipeId,
        jdbiRecipeInfo.name,
        user.name,
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

fun createTestCollection(tm: TransactionManager, ownerId: Int, collectionType: CollectionType) =
    tm.run {
        it.collectionRepository.createCollection(ownerId, randomUUID().toString().take(MAX_COLLECTION_NAME_LENGTH), collectionType)
    }

fun generateRandomUsername() = "test${Math.random()}".replace(".", "").take(MAX_USERNAME_LENGTH)
fun generateEmail(username: String) = "$username@email.com"
fun generateSecurePassword() = ("P" + randomUUID().toString()).take(MAX_PASSWORD_LENGTH)

fun generateRandomRecipeName() = randomUUID().toString().take(RecipeDomain.MAX_RECIPE_NAME_LENGTH)
fun generateRandomRecipeDescription() = randomUUID().toString().take(RecipeDomain.MAX_RECIPE_DESCRIPTION_LENGTH)
fun generateRandomRecipeIngredients() = listOf(
    Ingredient(randomUUID().toString().take(RecipeDomain.MAX_INGREDIENT_NAME_LENGTH), 1.0, IngredientUnit.TSP),
    Ingredient(randomUUID().toString().take(RecipeDomain.MAX_INGREDIENT_NAME_LENGTH), 1.0, IngredientUnit.TSP)
)
fun generateRandomRecipeInstructions() = Instructions(
    mapOf(
        "1" to randomUUID().toString().take(RecipeDomain.MAX_INSTRUCTIONS_STEP_LENGTH),
        "2" to randomUUID().toString().take(RecipeDomain.MAX_INSTRUCTIONS_STEP_LENGTH)
    )
)
