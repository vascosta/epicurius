package epicurius.domain.recipe

import org.springframework.stereotype.Component

@Component
class RecipeDomain {



    companion object {
        const val MIN_RECIPE_NAME_LENGTH = 3
        const val MAX_RECIPE_NAME_LENGTH = 25
        const val RECIPE_NAME_LENGTH_MSG = "must be between $MIN_RECIPE_NAME_LENGTH and $MAX_RECIPE_NAME_LENGTH characters"
        const val MAX_RECIPE_DESCRIPTION_LENGTH = 200
        const val RECIPE_DESCRIPTION_LENGTH_MSG = "must be less than $MAX_RECIPE_DESCRIPTION_LENGTH characters"
    }
}