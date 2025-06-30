package epicurius.unit.http.collection

import epicurius.domain.collection.CollectionProfile
import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionsNotAccessible
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.controllers.collection.models.output.GetCollectionsOutputModel
import epicurius.utils.generateRandomUsername
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GetCollectionsControllerTests : CollectionControllerTest() {

    private val limit = 10
    private val kitchenBookCollectionType = CollectionType.KITCHEN_BOOK

    @Test
    fun `Should retrieve collections from the user successfully`() {
        // given a collection type

        // mock
        val mockCollectionProfile = CollectionProfile(1, "Test Collection")
        whenever(
            collectionServiceMock.getCollections(testPublicAuthenticatedUser.user.id, null, kitchenBookCollectionType, null, limit)
        ).thenReturn(listOf(mockCollectionProfile))

        // when retrieving the collection
        val response = getCollections(testPublicAuthenticatedUser, null, kitchenBookCollectionType, null, limit)
        val body = response.body as GetCollectionsOutputModel

        // then the collection is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(body.collections.contains(mockCollectionProfile))
    }

    @Test
    fun `Should retrieve kitchen book collections from another user successfully`() {
        // given a username

        // mock
        val mockCollectionProfile = CollectionProfile(1, "Test Collection")
        whenever(
            collectionServiceMock.getCollections(
                testPublicAuthenticatedUser.user.id,
                testPrivateAuthenticatedUser.user.name,
                kitchenBookCollectionType,
                null,
                limit
            )
        ).thenReturn(listOf(mockCollectionProfile))

        // when retrieving the collection
        val response = getCollections(
            testPublicAuthenticatedUser,
            testPrivateAuthenticatedUser.user.name,
            kitchenBookCollectionType,
            null,
            limit
        )
        val body = response.body as GetCollectionsOutputModel

        // then the collection is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(body.collections.contains(mockCollectionProfile))
    }

    @Test
    fun `Should throw UserNotFound exception when retrieving the kitchen book collections from a non-existing user`() {
        // given a non-existing username
        val username = generateRandomUsername()

        // mock
        whenever(
            collectionServiceMock.getCollections(testPublicAuthenticatedUser.user.id, username, kitchenBookCollectionType, null, limit)
        ).thenThrow(UserNotFound(username))

        // when retrieving the user's collections
        assertFailsWith<UserNotFound> {
            getCollections(testPublicAuthenticatedUser, username, kitchenBookCollectionType, null, limit)
        }
    }

    @Test
    fun `Should throw CollectionsNotAccessible exception when retrieving the kitchen book collections from a private user not followed`() {
        // given a username
        val username = generateRandomUsername()

        // mock
        whenever(
            collectionServiceMock.getCollections(testPublicAuthenticatedUser.user.id, username, kitchenBookCollectionType, null, limit)
        ).thenThrow(CollectionsNotAccessible())

        // when retrieving the user's kitchen book collections
        assertFailsWith<CollectionsNotAccessible> {
            getCollections(testPublicAuthenticatedUser, username, kitchenBookCollectionType, null, limit)
        }
    }

}
