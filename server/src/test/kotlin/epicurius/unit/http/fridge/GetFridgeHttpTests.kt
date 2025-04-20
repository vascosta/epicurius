package epicurius.unit.http.fridge

import epicurius.domain.fridge.Fridge
import epicurius.http.fridge.models.output.FridgeOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class GetFridgeHttpTests : FridgeHttpTest() {

    @Test
    fun `Should get user's fridge successfully`() {
        // given a user with a fridge
        val authenticatedUser = testAuthenticatedUser

        // mock
        whenever(fridgeServiceMock.getFridge(authenticatedUser.user.id)).thenReturn(Fridge(emptyList()))

        // when getting the fridge
        val response = getFridge(authenticatedUser)

        // then the fridge is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(FridgeOutputModel(emptyList()), response.body)
    }
}
