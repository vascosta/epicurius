package epicurius.repository.jdbi.mappers

import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.mealPlanner.MealTime.Companion.fromInt
import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlanner
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class JdbiDailyMealPlannerMapper(
    private val jdbiRecipeInfoMapper: RowMapper<JdbiRecipeInfo>
) : RowMapper<JdbiDailyMealPlanner> {
    override fun map(rs: ResultSet, ctx: StatementContext): JdbiDailyMealPlanner {
        val setUp = JdbiDailyMealPlanner(
            date = rs.getDate("date"),
            meals = emptyMap()
        )

        val mealMap = mutableMapOf<MealTime, JdbiRecipeInfo>()
        do {
            val mealTime = MealTime.fromInt(rs.getInt("meal_time"))
            val recipeInfo = jdbiRecipeInfoMapper.map(rs, ctx)
            mealMap[mealTime] = recipeInfo
        } while (rs.next())

        return setUp.copy(meals = mealMap)
    }
}
