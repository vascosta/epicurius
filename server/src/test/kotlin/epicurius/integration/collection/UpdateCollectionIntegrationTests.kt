package epicurius.integration.collection

import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionAlreadyExists
import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.get
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.utils.createTestCollection
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UpdateCollectionIntegrationTests: CollectionIntegrationTest() {

    private val testUser = createTestUser(tm)
    private val testCollectionId = createTestCollection(tm, testUser.user.id, CollectionType.KITCHEN_BOOK)

    @Test
    fun `Should update a collection successfully with code 200`() {
        // given a collection and information to update it
        val newName = "Updated Collection Name"

        // when updating the collection
        val body = updateCollection(testUser.token, testCollectionId, newName)

        // then the collection is updated successfully with code 200
        assertNotNull(body)
        assertEquals(newName, body.collection.name)
    }

    @Test
    fun `Should fail with code 404 when updating a non-existing collection`() {
        // given a non-existing collection id anda information to update it
        val nonExistingCollectionId = 9999
        val name = "New Name"

        // when updating the collection
        val error = patch<Problem>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", nonExistingCollectionId.toString())),
            body = mapOf("name" to name),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token,
        )

        // then the collection is updated successfully with code 200
        val errorBody = getBody(error)
        assertEquals(CollectionNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 409 when updating a collection with an existing name`() {
        // given a collection and an existing name
        val collection = tm.run { it.collectionRepository.getCollectionById(testCollectionId) }

        // when updating the collection with an existing name
        assertNotNull(collection)
        val error = patch<Problem>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", testCollectionId.toString())),
            body = mapOf("name" to collection.name),
            responseStatus = HttpStatus.CONFLICT,
            token = testUser.token,
        )

        // then the collection is not updated and fails with code 409
        val errorBody = getBody(error)
        assertEquals(CollectionAlreadyExists().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 403 updating a collection that the user does not own`() {
        // given a collection that the user does not own
        val user = createTestUser(tm)
        val newName = "New Name"

        // when updating the collection
        val error = patch<Problem>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", testCollectionId.toString())),
            body = mapOf("name" to newName),
            responseStatus = HttpStatus.FORBIDDEN,
            token = user.token,
        )

        // then the collection is not updated and fails with code 403
        val errorBody = getBody(error)
        assertEquals(NotTheCollectionOwner().message, errorBody.detail)
    }

}