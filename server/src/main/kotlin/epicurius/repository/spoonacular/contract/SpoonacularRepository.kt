package epicurius.repository.spoonacular.contract

interface SpoonacularRepository {

    suspend fun getIngredients(partialName: String): List<String>
    suspend fun getSubstituteIngredients(name: String): List<String>
}
