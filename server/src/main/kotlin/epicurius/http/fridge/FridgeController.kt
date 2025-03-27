package epicurius.http.fridge

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.fridge.models.input.ProductInputModel
import epicurius.http.fridge.models.output.FridgeOutputModel
import epicurius.http.utils.Uris
import epicurius.services.FridgeService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class FridgeController(private val fridgeService: FridgeService) {

    @GetMapping(Uris.Fridge.GET_FRIDGE)
    fun getFridge(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val fridge = fridgeService.getFridge(authenticatedUser.userInfo.id)
        return ResponseEntity.ok().body(FridgeOutputModel(fridge.products))
    }

    @GetMapping(Uris.Fridge.GET_PRODUCTS)
    suspend fun getProductsList(
        authenticatedUser: AuthenticatedUser,
        @RequestParam partial: String
    ): ResponseEntity<*> {
        val productsList = fridgeService.getProductsList(partial)
        return ResponseEntity.ok().body(productsList)
    }

    @PostMapping(Uris.Fridge.ADD_PRODUCT)
    suspend fun addProducts(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: ProductInputModel
    ): ResponseEntity<*> {
        val newFridge = fridgeService.addProduct(authenticatedUser.userInfo.id, body)
        return ResponseEntity.ok().body(newFridge)
    }
}
