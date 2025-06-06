package epicurius.unit.http.collection

import epicurius.domain.collection.CollectionProfile
import epicurius.domain.collection.CollectionType
import epicurius.http.controllers.collection.models.output.GetCollectionsOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetCollectionsControllerTests : CollectionControllerTest() {

    @Test
    fun `Should retrieve multiple collections from a user successfully`() {
        // given a collection type
        val collectionType = CollectionType.KITCHEN_BOOK
        val limit = 1

        // mock
        val mockCollectionProfile = CollectionProfile(1, "Test Collection")
        whenever(
            collectionServiceMock.getCollections(testPublicAuthenticatedUser.user.id, collectionType, null, limit)
        ).thenReturn(listOf(mockCollectionProfile))

        // when retrieving the collection
        val response = getCollections(testPublicAuthenticatedUser, collectionType, null, limit)
        val body = response.body as GetCollectionsOutputModel

        // then the collection is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(body.collections.contains(mockCollectionProfile))
    }
}
