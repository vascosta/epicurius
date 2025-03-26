package epicurius.repository

interface SpoonacularRepository {

    suspend fun getProductsList(uri: String, partial: String): List<String>
}
