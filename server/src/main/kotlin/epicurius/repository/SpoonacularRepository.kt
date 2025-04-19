package epicurius.repository

interface SpoonacularRepository {

    suspend fun getProductsList(partialName: String): List<String>
}
