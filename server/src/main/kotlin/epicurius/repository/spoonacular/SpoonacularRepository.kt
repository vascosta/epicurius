package epicurius.repository.spoonacular

import epicurius.Environment
import epicurius.config.HttpClientConfigurer
import epicurius.domain.fridge.Ingredient
import epicurius.repository.spoonacular.contract.SpoonacularRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

@Component
class SpoonacularRepository(private val httpClient: HttpClientConfigurer) : SpoonacularRepository {

    override suspend fun getProductsList(partialName: String): List<String> {
        val validName = partialName.replace(" ", "-").lowercase()
        val uriCompleted = "$AUTOCOMPLETE_INGREDIENTS_URL?apiKey=$spoonacularApiKey&query=$validName"

        return withContext(Dispatchers.IO) {
            val request = async { httpClient.get(uriCompleted) }.await()
            val ingredientsList = Json.decodeFromString<List<Ingredient>>(request)
            ingredientsList.map { it.name.lowercase() }
        }
    }

    companion object {
        val spoonacularApiKey = Environment.getSpoonacularAPIKey().readAllBytes().decodeToString().trim()

        const val AUTOCOMPLETE_INGREDIENTS_URL = "https://api.spoonacular.com/food/ingredients/autocomplete"
        const val GET_INGREDIENT_SUBSTITUTES = "https://api.spoonacular.com/food/ingredients/substitutes"
    }
}
