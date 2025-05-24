package epicurius.integration.collection

import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionAlreadyExists
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.post
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreateCollectionIntegrationTests : CollectionIntegrationTest() {

    private val testUser = createTestUser(tm)

    @Test
    fun `Should create a collection and then retrieve it successfully with code 201`() {
        // given information for a new collection
        val name = "Test Collection"
        val type = CollectionType.KITCHEN_BOOK

        // when creating the collection
        val body = createCollection(testUser.token, name, type)

        // then the collection is created successfully with code 201
        assertNotNull(body)
        assertEquals(name, body.collection.name)
        assertEquals(type, body.collection.type)
    }

    @Test
    fun `Should fail with code 409 when creating a collection that already exists`() {
        // given information for a new collection
        val name = "Test Collection"
        val type = CollectionType.FAVOURITE
        createCollection(testUser.token, name, type)

        // when creating the collection
        val error = post<Problem>(
            client,
            api(Uris.Collection.COLLECTIONS),
            body = mapOf("name" to name, "type" to type),
            responseStatus = HttpStatus.CONFLICT,
            token = testUser.token
        )

        // then the collection is not created and fails with code 409
        val errorBody = getBody(error)
        assertEquals(CollectionAlreadyExists().message, errorBody.detail)
    }
}
