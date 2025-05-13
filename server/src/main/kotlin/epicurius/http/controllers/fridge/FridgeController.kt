package epicurius.http.controllers.fridge

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.fridge.models.input.ProductInputModel
import epicurius.http.controllers.fridge.models.input.UpdateProductInputModel
import epicurius.http.controllers.fridge.models.output.FridgeOutputModel
import epicurius.http.pipeline.authentication.AuthenticationRefreshHandler
import epicurius.http.pipeline.authentication.cookie.addCookie
import epicurius.http.utils.Uris
import epicurius.http.utils.Uris.Fridge.product
import epicurius.services.fridge.FridgeService
import jakarta.servlet.http.HttpServletResponse
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
class FridgeController(
    private val authenticationRefreshHandler: AuthenticationRefreshHandler,
    private val fridgeService: FridgeService
) {

    @GetMapping(Uris.Fridge.FRIDGE)
    fun getFridge(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val fridge = fridgeService.getFridge(authenticatedUser.user.id)
        return ResponseEntity
            .ok()
            .body(FridgeOutputModel(fridge.products))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @PostMapping(Uris.Fridge.FRIDGE)
    suspend fun addProducts(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: ProductInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val newFridge = fridgeService.addProduct(authenticatedUser.user.id, body)
        return ResponseEntity
            .created(product(newFridge.products.last().entryNumber))
            .body(newFridge)
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @PatchMapping(Uris.Fridge.PRODUCT)
    fun updateFridgeProduct(
        authenticatedUser: AuthenticatedUser,
        @PathVariable entryNumber: Int,
        @Valid @RequestBody body: UpdateProductInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val updatedFridge = fridgeService.updateProductInfo(authenticatedUser.user.id, entryNumber, body)
        return ResponseEntity
            .ok()
            .body(updatedFridge)
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @DeleteMapping(Uris.Fridge.PRODUCT)
    fun removeFridgeProduct(
        authenticatedUser: AuthenticatedUser,
        @PathVariable entryNumber: Int,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val updatedFridge = fridgeService.removeProduct(authenticatedUser.user.id, entryNumber)
        return ResponseEntity
            .ok()
            .body(updatedFridge)
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }
}
