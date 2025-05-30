package epicurius.repository.spoonacular

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
import java.io.FileInputStream

@Component
class SpoonacularRepository(private val httpClient: HttpClientConfigurer) : SpoonacularRepository {

    override suspend fun getIngredients(partialName: String): List<String> {
        val validName = partialName.replace(" ", "-").lowercase()
        println("Spoonacular API Key: $spoonacularApiKey")
        val uriCompleted = "$AUTOCOMPLETE_INGREDIENTS_URL?apiKey=$spoonacularApiKey&query=$validName"

        return withContext(Dispatchers.IO) {
            val request = async { httpClient.get(uriCompleted) }.await()
            val ingredients = Json.decodeFromString<List<SpoonacularIngredientModel>>(request)
            ingredients.map { it.name.lowercase() }
        }
    }

    override suspend fun getSubstituteIngredients(name: String): List<String> {
        val validName = name.replace(" ", "-").lowercase()
        val uriCompleted = "$GET_INGREDIENT_SUBSTITUTES_URL?apiKey=$spoonacularApiKey&ingredientName=$validName"

        return withContext(Dispatchers.IO) {
            val request = async { httpClient.get(uriCompleted) }.await()
            if (request.contains(SUBSTITUTES_NOT_FOUND)) {
                emptyList()
            } else if (request.contains(INGREDIENT_NOT_FOUND)) {
                throw InvalidIngredient(name)
            } else {
                val ingredients = Json.decodeFromString<SpoonacularSubstituteIngredientsModel>(request)
                ingredients.substitutes.map { it.substringAfter("= ").lowercase() }
            }
        }
    }

    companion object {
        val spoonacularAPIKeyFile = this::class.java.classLoader.getResourceAsStream("SpoonacularAPIKey.txt")
            ?: throw IllegalArgumentException("Spoonacular API key file not found")
        val spoonacularApiKey = spoonacularAPIKeyFile.bufferedReader().use { it.readText() }.trim()


        const val AUTOCOMPLETE_INGREDIENTS_URL = "https://api.spoonacular.com/food/ingredients/autocomplete"
        const val GET_INGREDIENT_SUBSTITUTES_URL = "https://api.spoonacular.com/food/ingredients/substitutes"

        const val SUBSTITUTES_NOT_FOUND = "Could not find any substitutes for that ingredient."
        const val INGREDIENT_NOT_FOUND = "Could not find any ingredient by that id."
    }
}
