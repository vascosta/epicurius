package epicurius.repository.jdbi.rateRecipe

import epicurius.repository.jdbi.rateRecipe.contract.RateRecipeRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.time.LocalDate

class JdbiRateRecipeRepository(private val handle: Handle) : RateRecipeRepository {

    override fun getRecipeRate(recipeId: Int): Double =
        handle.createQuery(
            """
                SELECT ROUND(AVG(rating), 1)
                FROM dbo.recipe_rating
                WHERE recipe_id = :recipeId
            """
        )
            .bind("recipeId", recipeId)
            .mapTo<Double>()
            .one()

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

    override fun updateRecipeRate(recipeId: Int, userId: Int, rating: Int) {
        handle.createUpdate(
            """
                UPDATE dbo.recipe_rating
                SET rating = :rating
                WHERE recipe_id = :recipeId AND user_id = :userId
            """
        )
            .bind("rating", rating)
            .bind("recipeId", recipeId)
            .bind("userId", userId)
            .execute()
    }

    override fun deleteRecipeRate(recipeId: Int, userId: Int) {
        handle.createUpdate(
            """
                DELETE FROM dbo.recipe_rating
                WHERE recipe_id = :recipeId AND user_id = :userId
            """
        )
            .bind("recipeId", recipeId)
            .bind("userId", userId)
            .execute()
    }

    override fun checkIfUserAlreadyRated(userId: Int, recipeId: Int): Boolean =
        handle.createQuery(
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
