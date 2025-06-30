package epicurius.integration.collection

import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionsNotAccessible
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.addQueryParams
import epicurius.integration.utils.get
import epicurius.utils.createTestCollection
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetCollectionsIntegrationTests : CollectionIntegrationTest() {

    private val testUser = createTestUser(tm)
    private val testCollectionId = createTestCollection(tm, testUser.user.id, CollectionType.KITCHEN_BOOK)
    private val testCollectionId2 = createTestCollection(tm, testUser.user.id, CollectionType.KITCHEN_BOOK)
    private val kitchenBookCollectionType = CollectionType.KITCHEN_BOOK
    private val limit = 2

    @Test
    fun `Should retrieve collections from the user successfully with code 200`() {
        // given a collection type

        // when retrieving the collections
        val body = getCollections(testUser.token, null, kitchenBookCollectionType, null, limit)

        // then the collections are retrieved successfully
        assertNotNull(body)
        assertNotNull(body.collections.find { it.id == testCollectionId })
        assertNotNull(body.collections.find { it.id == testCollectionId2 })
    }

    @Test
    fun `Should retrieve kitchen book collections from another user successfully with code 200`() {
        // given a username
        val user = createTestUser(tm)
        val collectionId = createTestCollection(tm, user.user.id, kitchenBookCollectionType)

        // when retrieving the collections
        val body = getCollections(testUser.token, user.user.name, kitchenBookCollectionType, null, limit)

        // then the collections are retrieved successfully
        assertNotNull(body)
        assertNotNull(body.collections.find { it.id == collectionId })
    }

    @Test
    fun `Should fail with code 404 when retrieving the kitchen book collections from a non-existing user`() {
        // given a non-existing username
        val username = "nonExistingUser"

        // when retrieving the collections
        val error = get<Problem>(
            client,
            api(Uris.Collection.COLLECTIONS).addQueryParams(
                mapOf(
                    "username" to username,
                    "collectionType" to kitchenBookCollectionType,
                    "limit" to limit
                )
            ),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )
        assertNotNull(error)

        // then the collections are not retrieved
        assertEquals(UserNotFound(username).message, error.detail)
    }

    @Test
    fun `Should fail with code 403 when retrieving the kitchen book collections from a private user not followed`() {
        // given a username
        val user = createTestUser(tm, true)

        // when retrieving the collections
        val error = get<Problem>(
            client,
            api(Uris.Collection.COLLECTIONS).addQueryParams(
                mapOf(
                    "username" to user.user.name,
                    "collectionType" to kitchenBookCollectionType,
                    "limit" to limit
                )
            ),
            responseStatus = HttpStatus.FORBIDDEN,
            token = testUser.token
        )
        assertNotNull(error)

        // then the collections are not retrieved
        assertEquals(CollectionsNotAccessible().message, error.detail)
    }
}
