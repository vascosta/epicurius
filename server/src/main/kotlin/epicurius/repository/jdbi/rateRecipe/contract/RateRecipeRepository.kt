package epicurius.repository.jdbi.rateRecipe.contract

interface RateRecipeRepository {

    fun getRecipeRating(recipeId: Int): Double
    fun getUserRecipeRating(recipeId: Int, userId: Int): Int
    fun rateRecipe(recipeId: Int, userId: Int, rating: Int): Double
    fun updateUserRecipeRating(recipeId: Int, userId: Int, rating: Int): Double
    fun deleteUserRecipeRating(recipeId: Int, userId: Int)

    fun checkIfUserAlreadyRated(userId: Int, recipeId: Int): Boolean
}
