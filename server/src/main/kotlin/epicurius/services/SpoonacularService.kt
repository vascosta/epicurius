package epicurius.services

import epicurius.repository.spoonacular.SpoonacularRepository
import org.springframework.stereotype.Component

@Component
class SpoonacularService(private val spoonacularWebClient: SpoonacularRepository) {

    suspend fun getAutocompleteProducts(partial: String): List<String> =
        spoonacularWebClient.getProductsList(
            "https://api.spoonacular.com/food/ingredients/autocomplete",
            partial
        )
}
