package epicurius.unit.services.collection

import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionsNotAccessible
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.UserNotFound
import epicurius.repository.jdbi.collection.models.JdbiCollectionProfileModel
import epicurius.unit.services.recipe.RecipeServiceTest.Companion.AUTHOR_ID
import epicurius.utils.generateRandomUsername
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class GetCollectionsServiceTests : CollectionServiceTest() {

    private val limit = 10
    private val kitchenBookCollectionType = CollectionType.KITCHEN_BOOK
    private val favouritesCollectionType = CollectionType.FAVOURITE

    @Test
    fun `Should retrieve collections from the user successfully`() {
        // given a collection type

        // mock
        val mockJdbiCollectionProfileModel = JdbiCollectionProfileModel(1, "Test Collection")
        whenever(jdbiCollectionRepositoryMock.getCollections(testPublicUser.id, favouritesCollectionType, null, limit))
            .thenReturn(listOf(mockJdbiCollectionProfileModel))

        // when retrieving the collections
        val collections = getCollections(testPublicUser.id, null, favouritesCollectionType, null, limit)

        // then the collections are retrieved successfully
        assertNotNull(collections.find { it.id == mockJdbiCollectionProfileModel.id })
    }

    @Test
    fun `Should retrieve kitchen book collections from another user successfully`() {
        // given a username

        // mock
        val mockJdbiCollectionProfileModel = JdbiCollectionProfileModel(1, "Test Collection")
        whenever(jdbiUserRepositoryMock.getUser(testPrivateUser.name)).thenReturn(testPrivateUser)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(testPrivateUser.name, testPublicUser.id)).thenReturn(true)
        whenever(jdbiCollectionRepositoryMock.getCollections(testPrivateUser.id, kitchenBookCollectionType, null, limit))
            .thenReturn(listOf(mockJdbiCollectionProfileModel))

        // when retrieving the collections
        val collections = getCollections(testPublicUser.id, testPrivateUser.name, kitchenBookCollectionType, null, limit)

        // then the collections are retrieved successfully
        assertNotNull(collections.find { it.id == mockJdbiCollectionProfileModel.id })
    }

    @Test
    fun `Should throw UserNotFound exception when retrieving the kitchen book collections from a non-existing user`() {
        // given a non-existing username
        val username = generateRandomUsername()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(username)).thenReturn(null)

        // when retrieving the user's collections
        // then the collections are not retrieved and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            runBlocking { getCollections(testPublicUser.id, username, favouritesCollectionType, null, limit) }
        }
    }

    @Test
    fun `Should throw CollectionsNotAccessible exception when retrieving the kitchen book collections from a private user not followed`() {
        // given a username
        val username = generateRandomUsername()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(username)).thenReturn(testPrivateUser)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(username, testPublicUser.id)).thenReturn(false)

        // when retrieving the user's kitchen book collections
        // then the collections are not retrieved and throws CollectionsNotAccessible exception
        assertFailsWith<CollectionsNotAccessible> {
            runBlocking { getCollections(testPublicUser.id, username, favouritesCollectionType, null, limit) }
        }
    }
}
