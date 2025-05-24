package epicurius.http.controllers.rateRecipe.models.input

import epicurius.domain.recipe.utils.Companion.MAX_RATING
import epicurius.domain.recipe.utils.Companion.MIN_RATING
import epicurius.domain.recipe.utils.Companion.RATING_MSG
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class RateRecipeInputModel(
    @field:Min(MIN_RATING.toLong(), message = RATING_MSG)
    @field:Max(MAX_RATING.toLong(), message = RATING_MSG)
    val rating: Int
)
