package epicurius.unit.http.collection

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.collection.Collection
import epicurius.domain.collection.CollectionType
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.unit.http.HttpTest
import epicurius.unit.services.collection.CollectionServiceTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomRecipeName
import epicurius.utils.generateRandomUsername
import java.util.UUID.randomUUID

open class CollectionHttpTest : HttpTest() {

    companion object {
        const val FAVOURITE_COLLECTION_ID = 1
        const val KITCHEN_BOOK_COLLECTION_ID = FAVOURITE_COLLECTION_ID + 1
        const val RECIPE_ID = 1
        private val publicAuthenticatedUsername = generateRandomUsername()
        private val token = randomUUID().toString()

        val testPublicAuthenticatedUser = AuthenticatedUser(
            User(
                1,
                publicAuthenticatedUsername,
                generateEmail(publicAuthenticatedUsername),
                userDomain.encodePassword(randomUUID().toString()),
                userDomain.hashToken(token),
                "PT",
                false,
                listOf(Intolerance.GLUTEN),
                listOf(Diet.GLUTEN_FREE),
                randomUUID().toString()
            ),
            token,
        )

        private val privateAuthenticatedUsername = generateRandomUsername()
        val testPrivateAuthenticatedUser = testPublicAuthenticatedUser.copy(
            user = testPublicAuthenticatedUser.user.copy(
                id = 2,
                name = privateAuthenticatedUsername,
                email = generateEmail(privateAuthenticatedUsername),
                privacy = true,
            ),
        )

        val testFavouriteCollection = Collection(
            FAVOURITE_COLLECTION_ID, "Test Collection", CollectionType.FAVOURITE, emptyList()
        )

        val testKitchenBookCollection = CollectionServiceTest.Companion.testFavouriteCollection.copy(
            id = KITCHEN_BOOK_COLLECTION_ID, type = CollectionType.KITCHEN_BOOK
        )

        val testRecipeInfo = RecipeInfo(
            RECIPE_ID,
            generateRandomRecipeName(),
            Cuisine.MEDITERRANEAN,
            MealType.SOUP,
            1,
            1,
            byteArrayOf()
        )
    }
}
