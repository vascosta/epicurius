package epicurius.http.controllers.collection.models.input

import epicurius.domain.user.UserDomain
import jakarta.validation.constraints.Positive

data class AddRecipeToCollectionInputModel(
    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val recipeId: Int
)
