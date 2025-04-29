package epicurius.repository.spoonacular

import epicurius.Environment
import epicurius.config.HttpClientConfigurer
import epicurius.domain.exceptions.InvalidIngredient
import epicurius.repository.spoonacular.contract.SpoonacularRepository
import epicurius.repository.spoonacular.models.SpoonacularIngredientModel
import epicurius.repository.spoonacular.models.SpoonacularSubstituteIngredientsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

@Component
class SpoonacularRepository(private val httpClient: HttpClientConfigurer) : SpoonacularRepository {

    override suspend fun getIngredients(partialName: String): List<String> {
        val validName = partialName.replace(" ", "-").lowercase()
        val uriCompleted = "$AUTOCOMPLETE_INGREDIENTS_URL?apiKey=$spoonacularApiKey&query=$validName"

        return withContext(Dispatchers.IO) {
            val request = async { httpClient.get(uriCompleted) }.await()
            println(request)
            val ingredients = Json.decodeFromString<List<SpoonacularIngredientModel>>(request)
            ingredients.map { it.name.lowercase() }
        }
    }

    override suspend fun getSubstituteIngredients(name: String): List<String> {
        val validName = name.replace(" ", "-").lowercase()
        val uriCompleted = "$GET_INGREDIENT_SUBSTITUTES?apiKey=$spoonacularApiKey&ingredientName=$validName"

        return withContext(Dispatchers.IO) {
            val request = async { httpClient.get(uriCompleted) }.await()
            if (request.contains("failure")) {
                emptyList<String>()
            }
            val ingredients = Json.decodeFromString<SpoonacularSubstituteIngredientsModel>(request)
            ingredients.substitutes.map { it.lowercase() }
        }
    }

    companion object {
        val spoonacularApiKey = Environment.getSpoonacularAPIKey().readAllBytes().decodeToString().trim()

        const val AUTOCOMPLETE_INGREDIENTS_URL = "https://api.spoonacular.com/food/ingredients/autocomplete"
        const val GET_INGREDIENT_SUBSTITUTES = "https://api.spoonacular.com/food/ingredients/substitutes"
    }
}
