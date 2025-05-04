package epicurius.repository.jdbi.rateRecipe

import epicurius.repository.jdbi.rateRecipe.contract.RateRecipeRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.time.LocalDate

class JdbiRateRecipeRepository(private val handle: Handle): RateRecipeRepository {

    override fun rateRecipe(recipeId: Int, userId: Int, rating: Int) {
        handle.createUpdate(
            """
                INSERT INTO dbo.recipe_rating (recipe_id, user_id, rating, created_at)
                VALUES (:recipeId, :userId, :rating, :createdAt)
            """
        )
            .bind("recipeId", recipeId)
            .bind("userId", userId)
            .bind("rating", rating)
            .bind("createdAt", LocalDate.now())
            .execute()
    }

    override fun checkIfUserAlreadyRated(userId: Int, recipeId: Int): Boolean {
        return handle.createQuery(
            """
                SELECT COUNT(*) > 0
                FROM dbo.recipe_rating
                WHERE user_id = :userId AND recipe_id = :recipeId
            """
        )
            .bind("userId", userId)
            .bind("recipeId", recipeId)
            .mapTo<Boolean>()
            .one()
    }
}