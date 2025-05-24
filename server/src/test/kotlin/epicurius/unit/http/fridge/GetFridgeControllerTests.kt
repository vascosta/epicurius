package epicurius.unit.http.fridge

import epicurius.domain.fridge.Fridge
import epicurius.http.controllers.fridge.models.output.GetFridgeOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class GetFridgeControllerTests : FridgeHttpTest() {

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
        assertEquals(GetFridgeOutputModel(emptyList()), response.body)
    }

    @Test
    fun `Should get user's fridge with products successfully`() {
        // given a user with a fridge
        val authenticatedUser = testAuthenticatedUser

        // mock
        whenever(fridgeServiceMock.getFridge(authenticatedUser.user.id)).thenReturn(Fridge(listOf(product)))

        // when getting the fridge
        val response = getFridge(authenticatedUser)

        // then the fridge is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GetFridgeOutputModel(listOf(product)), response.body)
    }
}
