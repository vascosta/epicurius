package epicurius.integration.collection

import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.delete
import epicurius.integration.utils.getBody
import epicurius.utils.createTestCollection
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteCollectionIntegrationTests : CollectionIntegrationTest() {

    private val testUser = createTestUser(tm)

    @Test
    fun `Should delete a collection successfully with code 204`() {
        // given a collection
        val collectionId = createTestCollection(tm, testUser.user.id, CollectionType.KITCHEN_BOOK)

        // when deleting the collection
        // then the collection is deleted successfully with code 204
        deleteCollection(testUser.token, collectionId)
    }

    @Test
    fun `Should fail with code 404 when deleting a non-existing collection`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 9999

        // when deleting the collection
        val error = delete<Problem>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", nonExistingCollectionId.toString())),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )

        // then the collection is not found and fails with code 404
        val errorBody = getBody(error)
        assertEquals(CollectionNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 403 when deleting a collection that the user does not own`() {
        // given a collection id that the user does not own
        val user = createTestUser(tm)
        val collectionId = createTestCollection(tm, user.user.id, CollectionType.FAVOURITE)

        // when deleting the collection with a different user
        val error = delete<Problem>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", collectionId.toString())),
            responseStatus = HttpStatus.FORBIDDEN,
            token = testUser.token
        )

        // then the collection is not deleted and fails with code 403
        val errorBody = getBody(error)
        assertEquals(NotTheCollectionOwner().message, errorBody.detail)
    }
}
