package epicurius.repository.cloudFunction.manager

import epicurius.config.HttpClientConfigurer
import epicurius.repository.cloudFunction.CloudFunctionRepository
import org.springframework.stereotype.Repository

@Repository
class CloudFunctionManager(httpClient: HttpClientConfigurer) {
    val cloudFunctionRepository = CloudFunctionRepository(httpClient)
}
