package epicurius.integration.collection

import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionNotAccessible
import epicurius.domain.exceptions.CollectionNotFound
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.get
import epicurius.utils.createTestCollection
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetCollectionIntegrationTests : CollectionIntegrationTest() {

    val testUser = createTestUser(tm)
    val privateTestUser = createTestUser(tm, true)

    @Test
    fun `Should retrieve a collection successfully with code 200`() {
        // given a collection id and a user (testUser)
        val collectionId = createTestCollection(tm, testUser.user.id, CollectionType.KITCHEN_BOOK)

        // when retrieving the collection
        val body = getCollection(testUser.token, collectionId)

        // then the collection is retrieved successfully with code 200
        assertNotNull(body)
        assertEquals(collectionId, body.collection.id)
    }

    @Test
    fun `Should fail with code 404 when retrieving a collection that does not exist`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 9999

        // when retrieving the collection
        val error = get<Problem>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", nonExistingCollectionId.toString())),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )

        // then the collection is not found and fails with code 404
        assertNotNull(error)
        assertEquals(CollectionNotFound().message, error.detail)
    }

    @Test
    fun `Should fail with code 403 when retrieving a favourite's collection of another user`() {
        // given a collection id
        val collectionId = createTestCollection(tm, privateTestUser.user.id, CollectionType.FAVOURITE)

        // when retrieving the collection
        val error = get<Problem>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", collectionId.toString())),
            responseStatus = HttpStatus.FORBIDDEN,
            token = testUser.token
        )

        // then the collection is not accessible and fails with code 403
        assertNotNull(error)
        assertEquals(CollectionNotAccessible().message, error.detail)
    }

    @Test
    fun `Should fail with code 403 when retrieving a kitchen book's collection of a private user not being followed`() {
        // given a collection id
        val collectionId = createTestCollection(tm, privateTestUser.user.id, CollectionType.KITCHEN_BOOK)

        // when retrieving the collection
        val error = get<Problem>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", collectionId.toString())),
            responseStatus = HttpStatus.FORBIDDEN,
            token = testUser.token
        )

        // then the collection is not accessible and fails with code 403
        assertNotNull(error)
        assertEquals(CollectionNotAccessible().message, error.detail)
    }
}
