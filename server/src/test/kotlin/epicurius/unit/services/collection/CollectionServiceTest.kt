package epicurius.unit.services.collection

import epicurius.domain.collection.Collection
import epicurius.domain.collection.CollectionType
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.user.User
import epicurius.repository.jdbi.collection.models.JdbiCollectionModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.unit.services.ServiceTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomRecipeIngredients
import epicurius.utils.generateRandomRecipeName
import epicurius.utils.generateRandomUsername
import java.time.LocalDate

open class CollectionServiceTest: ServiceTest() {

    companion object {
        const val FAVOURITE_COLLECTION_ID = 1
        const val KITCHEN_BOOK_COLLECTION_ID = FAVOURITE_COLLECTION_ID + 1
        const val RECIPE_ID = 1
        const val PUBLIC_USER_ID = 1
        const val PRIVATE_USER_ID = PUBLIC_USER_ID + 1

        val testPublicUsername = generateRandomUsername()
        val testPublicUser = User(
            PUBLIC_USER_ID,
            testPublicUsername,
            generateEmail(testPublicUsername),
            "",
            "",
            "PT",
            false,
            emptyList(),
            emptyList(),
            null
        )
        val testPrivateUsername = generateRandomUsername()
        val testPrivateUser = testPublicUser.copy(
            id = PRIVATE_USER_ID,
            name = testPrivateUsername,
            email = generateEmail(testPrivateUsername),
            privacy = true
        )

        val testFavouriteJdbiCollectionModel = JdbiCollectionModel(
            FAVOURITE_COLLECTION_ID, PUBLIC_USER_ID, "Test Collection", CollectionType.FAVOURITE, emptyList()
        )

        val testKitchenBookJdbiCollectionModel = testFavouriteJdbiCollectionModel.copy(
            id = KITCHEN_BOOK_COLLECTION_ID, type = CollectionType.KITCHEN_BOOK
        )

        val testFavouriteCollection = Collection(
            FAVOURITE_COLLECTION_ID, "Test Collection", CollectionType.FAVOURITE, emptyList()
        )

        val testKitchenBookCollection = testFavouriteCollection.copy(
            id = FAVOURITE_COLLECTION_ID + 1, type = CollectionType.KITCHEN_BOOK
        )

        val testJdbiRecipeModel = JdbiRecipeModel(
            RECIPE_ID,
            generateRandomRecipeName(),
            testPublicUser.id,
            testPublicUsername,
            LocalDate.now(),
            1,
            1,
            Cuisine.MEDITERRANEAN,
            MealType.SOUP,
            emptyList(),
            emptyList(),
            generateRandomRecipeIngredients(),
            picturesNames = listOf("")
        )

        val testJdbiRecipeInfo = JdbiRecipeInfo(
            RECIPE_ID,
            testJdbiRecipeModel.name,
            testJdbiRecipeModel.cuisine,
            testJdbiRecipeModel.mealType,
            testJdbiRecipeModel.preparationTime,
            testJdbiRecipeModel.servings,
            testJdbiRecipeModel.picturesNames,
        )

        val testRecipeInfo = testJdbiRecipeInfo.toRecipeInfo(byteArrayOf())
    }
}