package epicurius.repository.jdbi.mappers

import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.mealPlanner.MealTime.Companion.fromInt
import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlannerRow
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class JdbiDailyMealPlannerRowMapper(
    private val jdbiRecipeInfoMapper: RowMapper<JdbiRecipeInfo>
) : RowMapper<JdbiDailyMealPlannerRow> {
    override fun map(rs: ResultSet, ctx: StatementContext): JdbiDailyMealPlannerRow {
        return JdbiDailyMealPlannerRow(
            date = rs.getDate("date").toLocalDate(),
            maxCalories = rs.getInt("max_calories"),
            mealTime = MealTime.fromInt(rs.getInt("meal_time")),
            jdbiRecipeInfo = jdbiRecipeInfoMapper.map(rs, ctx)
        )
    }
}
