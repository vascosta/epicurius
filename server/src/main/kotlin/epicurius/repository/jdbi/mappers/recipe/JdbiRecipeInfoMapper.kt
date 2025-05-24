package epicurius.repository.jdbi.mappers.recipe

import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.utils.getArray
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class JdbiRecipeInfoMapper : RowMapper<JdbiRecipeInfo> {
    override fun map(rs: ResultSet, ctx: StatementContext): JdbiRecipeInfo {
        val cuisine = Cuisine.Companion.fromInt(rs.getInt("cuisine"))
        val mealType = MealType.Companion.fromInt(rs.getInt("meal_type"))

        val array = rs.getArray("pictures_names")
        val pictures: List<String> = if (array != null) {
            getArray<String>(array).toList()
        } else {
            emptyList()
        }

        return JdbiRecipeInfo(
            id = rs.getInt("recipe_id"),
            name = rs.getString("recipe_name") ?: "",
            cuisine = cuisine,
            mealType = mealType,
            preparationTime = rs.getInt("preparation_time"),
            servings = rs.getInt("servings"),
            picturesNames = pictures
        )
    }
}
