package epicurius.repository.cloudFunction.contract

interface CloudFunctionRepository {
    suspend fun getIngredientsFromPicture(pictureName: String): List<String>
}
