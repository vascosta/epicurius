package epicurius.repository.jdbi.mappers

import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Cuisine.Companion.fromInt
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.MealType.Companion.fromInt
import epicurius.domain.recipe.RecipeInfo
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class RecipeInfoMapper : RowMapper<RecipeInfo> {
    override fun map(rs: ResultSet, ctx: StatementContext): RecipeInfo {
        val cuisine = Cuisine.fromInt(rs.getInt("cuisine"))
        val mealType = MealType.fromInt(rs.getInt("meal_type"))
        return RecipeInfo(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            cuisine = cuisine,
            mealType = mealType,
            preparationTime = rs.getInt("preparation_time"),
            servings = rs.getInt("servings")
        )
    }
}
