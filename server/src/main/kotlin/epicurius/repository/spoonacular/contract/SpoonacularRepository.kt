package epicurius.repository.spoonacular.contract

interface SpoonacularRepository {

    suspend fun getProductsList(partialName: String): List<String>
}
