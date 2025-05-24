package epicurius.repository.cloudFunction

import epicurius.config.HttpClientConfigurer
import epicurius.domain.exceptions.CloudFunctionException
import epicurius.repository.cloudFunction.contract.CloudFunctionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CloudFunctionRepository(private val httpClient: HttpClientConfigurer) : CloudFunctionRepository {

    override suspend fun getIngredientsFromPicture(pictureName: String): List<String> {
        return withContext(Dispatchers.IO) {
            val requestBody = httpClient.post(CLOUD_FUNCTION_ENDPOINT, mapOf("pictureName" to pictureName))
            if (requestBody.contains("error")) {
                throw CloudFunctionException(requestBody)
            }
            val ingredients = Json.decodeFromString<List<String>>(requestBody)
            ingredients.map { it.lowercase() }
        }
    }

    companion object {
        private const val CLOUD_FUNCTION_ENDPOINT = "https://europe-west1-ps-2425.cloudfunctions.net/get-ingredients-from-picture"
    }
}
