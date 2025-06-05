package epicurius.repository.spoonacular.manager

import epicurius.config.HttpClientConfigurer
import epicurius.repository.spoonacular.SpoonacularRepository
import org.springframework.stereotype.Repository

@Repository
class SpoonacularManager(httpClient: HttpClientConfigurer) {
    val spoonacularRepository = SpoonacularRepository(httpClient)
}
