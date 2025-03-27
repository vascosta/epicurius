package epicurius.repository.spoonacular

import epicurius.config.HttpClientConfigurer
import org.springframework.stereotype.Component

@Component
class SpoonacularManager(httpClient: HttpClientConfigurer) {
    val spoonacularRepository = SpoonacularRepository(httpClient)
}
