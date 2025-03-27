package epicurius.repository.spoonacular

import epicurius.Environment
import epicurius.config.HttpClientConfigurer
import epicurius.domain.fridge.Ingredient
import epicurius.repository.SpoonacularRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

@Component
class SpoonacularRepository(private val httpClient: HttpClientConfigurer) : SpoonacularRepository {

    override suspend fun getProductsList(partial: String): List<String> {
        val uriCompleted = "$AUTOCOMPLETE_INGREDIENTS?apiKey=$spoonacularApiKey&query=$partial"

        return withContext(Dispatchers.IO) {
            val request = async { httpClient.get(uriCompleted) }.await()
            val ingredientsList = Json.decodeFromString<List<Ingredient>>(request)
            ingredientsList.map { it.name }
        }
    }

    companion object {
        val spoonacularApiKey = Environment.getSpoonacularAPIKey().readAllBytes().decodeToString().trim()

        const val AUTOCOMPLETE_INGREDIENTS = "https://api.spoonacular.com/food/ingredients/autocomplete"
    }
}
