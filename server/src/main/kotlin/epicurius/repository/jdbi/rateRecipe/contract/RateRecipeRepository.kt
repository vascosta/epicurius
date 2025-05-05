package epicurius.repository.jdbi.rateRecipe.contract

interface RateRecipeRepository {

    fun getRecipeRate(recipeId: Int): Double
    fun rateRecipe(recipeId: Int, userId: Int, rating: Int)
    fun updateRecipeRate(recipeId: Int, userId: Int, rating: Int)
    fun deleteRecipeRate(recipeId: Int, userId: Int)

    fun checkIfUserAlreadyRated(userId: Int, recipeId: Int): Boolean
}
