package epicurius.repository.jdbi.rateRecipe.contract

interface RateRecipeRepository {

    fun rateRecipe(recipeId: Int, userId: Int, rating: Int)

    fun checkIfUserAlreadyRated(userId: Int, recipeId: Int): Boolean
}