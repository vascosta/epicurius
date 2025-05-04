package epicurius.repository.jdbi.mappers

import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.utils.getArray
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class JdbiRecipeInfoMapper : RowMapper<JdbiRecipeInfo> {
    override fun map(rs: ResultSet, ctx: StatementContext): JdbiRecipeInfo {
        val cuisine = Cuisine.fromInt(rs.getInt("cuisine"))
        val mealType = MealType.fromInt(rs.getInt("meal_type"))
        val pictures = getArray<String>(rs.getArray("pictures_names")).toList()
        return JdbiRecipeInfo(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            cuisine = cuisine,
            mealType = mealType,
            preparationTime = rs.getInt("preparation_time"),
            servings = rs.getInt("servings"),
            pictures = pictures
        )
    }
}
