package epicurius.repository.cloudFunction

import epicurius.config.HttpClientConfigurer
import epicurius.repository.cloudFunction.contract.CloudFunctionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CloudFunctionRepository(private val httpClient: HttpClientConfigurer) : CloudFunctionRepository {

    // refactor in the future when the cloud function is deployed
    override suspend fun getIngredientsFromPicture(pictureName: String): List<String> {
        val localUri = "http://localhost:1904"

        return withContext(Dispatchers.IO) {
            val requestBody = httpClient.post(localUri, mapOf("pictureName" to pictureName))
            val ingredients = Json.decodeFromString<List<String>>(requestBody)
            ingredients.map { it.lowercase() }
        }
    }
}
