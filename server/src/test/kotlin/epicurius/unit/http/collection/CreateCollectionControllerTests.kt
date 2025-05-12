package epicurius.unit.http.collection

import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionAlreadyExists
import epicurius.http.controllers.collection.models.input.CreateCollectionInputModel
import epicurius.http.controllers.collection.models.output.CreateCollectionOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateCollectionControllerTests : CollectionHttpTest() {

    private val createCollectionInputInfo = CreateCollectionInputModel("Test Collection", CollectionType.FAVOURITE)

    @Test
    fun `Should create a collection and then retrieve it successfully`() {
        // given information for a new collection (createCollectionInputInfo)

        // mock

        whenever(collectionServiceMock.createCollection(testPublicAuthenticatedUser.user.id, createCollectionInputInfo))
            .thenReturn(testFavouriteCollection)
        whenever(authenticationRefreshHandlerMock.refreshToken(testPublicAuthenticatedUser.token)).thenReturn(mockCookie)

        // when creating the collection
        val response = createCollection(testPublicAuthenticatedUser, createCollectionInputInfo, mockResponse)
        val body = response.body as CreateCollectionOutputModel

        // then the collection is created successfully
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(testFavouriteCollection, body.collection)
    }

    @Test
    fun `Should throw CollectionAlreadyExists when creating a collection that already exists`() {
        // given information for a new collection (createCollectionInputInfo)

        // mock
        whenever(collectionServiceMock.createCollection(testPublicAuthenticatedUser.user.id, createCollectionInputInfo))
            .thenThrow(CollectionAlreadyExists())

        // when creating the collection
        // then the collection is not created and throws CollectionAlreadyExists exception
        assertFailsWith<CollectionAlreadyExists> {
            createCollection(testPublicAuthenticatedUser, createCollectionInputInfo, mockResponse)
        }
    }
}
