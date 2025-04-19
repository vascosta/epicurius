package epicurius.repository.spoonacular.manager

import epicurius.config.HttpClientConfigurer
import epicurius.repository.spoonacular.SpoonacularRepository
import org.springframework.stereotype.Component

@Component
class SpoonacularManager(httpClient: HttpClientConfigurer) {
    val spoonacularRepository = SpoonacularRepository(httpClient)
}
