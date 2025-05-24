package epicurius.http.controllers.fridge

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.fridge.models.input.AddProductInputModel
import epicurius.http.controllers.fridge.models.input.UpdateProductInputModel
import epicurius.http.controllers.fridge.models.output.AddProductOutputModel
import epicurius.http.controllers.fridge.models.output.GetFridgeOutputModel
import epicurius.http.controllers.fridge.models.output.RemoveProductOutputModel
import epicurius.http.controllers.fridge.models.output.UpdateProductOutputModel
import epicurius.http.utils.Uris
import epicurius.http.utils.Uris.Fridge.product
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class FridgeController(private val fridgeService: FridgeService) {

    @GetMapping(Uris.Fridge.FRIDGE)
    fun getFridge(
        authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<*> {
        val fridge = fridgeService.getFridge(authenticatedUser.user.id)
        return ResponseEntity
            .ok()
            .body(GetFridgeOutputModel(fridge.products))
    }

    @PostMapping(Uris.Fridge.FRIDGE)
    suspend fun addProduct(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: AddProductInputModel,
    ): ResponseEntity<*> {
        val newFridge = fridgeService.addProduct(authenticatedUser.user.id, body)
        return ResponseEntity
            .created(product(newFridge.products.last().entryNumber))
            .body(AddProductOutputModel(newFridge))
    }

    @PatchMapping(Uris.Fridge.PRODUCT)
    fun updateFridgeProduct(
        authenticatedUser: AuthenticatedUser,
        @PathVariable entryNumber: Int,
        @Valid @RequestBody body: UpdateProductInputModel,
    ): ResponseEntity<*> {
        val updatedFridge = fridgeService.updateProductInfo(authenticatedUser.user.id, entryNumber, body)
        return ResponseEntity
            .ok()
            .body(UpdateProductOutputModel(updatedFridge))
    }

    @DeleteMapping(Uris.Fridge.PRODUCT)
    fun removeFridgeProduct(
        authenticatedUser: AuthenticatedUser,
        @PathVariable entryNumber: Int,
    ): ResponseEntity<*> {
        val updatedFridge = fridgeService.removeProduct(authenticatedUser.user.id, entryNumber)
        return ResponseEntity
            .ok()
            .body(RemoveProductOutputModel(updatedFridge))
    }
}
