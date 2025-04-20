package epicurius.repository.cloudFunction.contract

interface CloudFunctionRepository {
    fun getIngredientsFromPicture(pictureName: String): List<String>
}
