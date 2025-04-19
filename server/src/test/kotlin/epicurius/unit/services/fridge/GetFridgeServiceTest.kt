package epicurius.unit.services.fridge

import epicurius.domain.fridge.Fridge
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.kotlin.whenever
import kotlin.test.Test

class GetFridgeServiceTest : FridgeServiceTest() {

    @Test
    fun `Should retrieve the user's fridge successfully`() {
        // given a user fridge
        val fridge = Fridge(emptyList())

        // mock
        whenever(jdbiFridgeRepositoryMock.getFridge(USER_ID)).thenReturn(fridge)

        // when retrieving the fridge
        val retrievedFridge = fridgeService.getFridge(USER_ID)

        // then the fridge is retrieved successfully
        assertEquals(fridge, retrievedFridge)
        assertEquals(fridge.products, retrievedFridge.products)
        assertTrue(retrievedFridge.products.isEmpty())
    }
}
