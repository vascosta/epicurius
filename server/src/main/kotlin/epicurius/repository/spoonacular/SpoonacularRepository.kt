package epicurius.repository.spoonacular

import epicurius.Environment
import epicurius.HttpClientConfigurer
import epicurius.domain.fridge.Ingredient
import epicurius.repository.SpoonacularRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

@Component
class SpoonacularRepository(private val httpClient: HttpClientConfigurer): SpoonacularRepository {

    override suspend fun getProductsList(uri: String, partial:String): List<String> {
        val full = "$uri?apiKey=$spoonacularApiKey&query=$partial"

        return withContext(Dispatchers.IO) {
            val request = async { httpClient.get(full) }.await()
            val ingredientsList = Json.decodeFromString<List<Ingredient>>(request)
            ingredientsList.map { it.name }
        }
    }

    companion object {
        val spoonacularApiKey =  Environment.getSpoonacularAPIKey().readAllBytes().decodeToString().trim()
    }
}

