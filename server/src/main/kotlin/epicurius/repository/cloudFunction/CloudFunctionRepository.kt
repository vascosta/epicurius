package epicurius.repository.cloudFunction

import epicurius.config.HttpClientConfigurer
import epicurius.repository.cloudFunction.contract.CloudFunctionRepository

class CloudFunctionRepository(private val httpClient: HttpClientConfigurer): CloudFunctionRepository {
    override fun getIngredientsFromPicture(pictureName: String): List<String> {
        TODO("Not yet implemented")
    }
}