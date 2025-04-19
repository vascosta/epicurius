package epicurius.http.fridge

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.fridge.models.input.OpenProductInputModel
import epicurius.http.fridge.models.input.ProductInputModel
import epicurius.http.fridge.models.input.UpdateProductInputModel
import epicurius.http.fridge.models.output.FridgeOutputModel
import epicurius.http.utils.Uris
import epicurius.services.fridge.FridgeService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class FridgeController(private val fridgeService: FridgeService) {

    @GetMapping(Uris.Fridge.FRIDGE)
    fun getFridge(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val fridge = fridgeService.getFridge(authenticatedUser.user.id)
        return ResponseEntity.ok().body(FridgeOutputModel(fridge.products))
    }

    @GetMapping(Uris.Fridge.PRODUCTS)
    suspend fun getProductsList(
        authenticatedUser: AuthenticatedUser,
        @RequestParam partial: String
    ): ResponseEntity<*> {
        val productsList = fridgeService.getProductsList(partial)
        return ResponseEntity.ok().body(productsList)
    }

    @PostMapping(Uris.Fridge.FRIDGE)
    suspend fun addProducts(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: ProductInputModel
    ): ResponseEntity<*> {
        val newFridge = fridgeService.addProduct(authenticatedUser.user.id, body)
        return ResponseEntity.ok().body(newFridge)
    }

    @PatchMapping(Uris.Fridge.PRODUCT)
    fun updateFridgeProduct(
        authenticatedUser: AuthenticatedUser,
        @PathVariable entryNumber: Int,
        @Valid @RequestBody body: UpdateProductInputModel
    ): ResponseEntity<*> {
        val updatedFridge = fridgeService.updateProductInfo(authenticatedUser.user.id, entryNumber, body)
        return ResponseEntity.ok().body(updatedFridge)
    }

    @PatchMapping(Uris.Fridge.OPEN_PRODUCT)
    fun openFridgeProduct(
        authenticatedUser: AuthenticatedUser,
        @PathVariable entryNumber: Int,
        @Valid @RequestBody body: OpenProductInputModel
    ): ResponseEntity<*> {
        val updatedFridge = fridgeService.openProduct(authenticatedUser.user.id, entryNumber, body)
        return ResponseEntity.ok().body(updatedFridge)
    }

    @DeleteMapping(Uris.Fridge.PRODUCT)
    fun removeFridgeProduct(
        authenticatedUser: AuthenticatedUser,
        @PathVariable entryNumber: Int
    ): ResponseEntity<*> {
        val updatedFridge = fridgeService.removeProduct(authenticatedUser.user.id, entryNumber)
        return ResponseEntity.ok().body(updatedFridge)
    }
}
