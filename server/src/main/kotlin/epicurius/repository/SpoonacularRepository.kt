package epicurius.repository

interface SpoonacularRepository {

    suspend fun getProductsList(partial: String): List<String>
}
