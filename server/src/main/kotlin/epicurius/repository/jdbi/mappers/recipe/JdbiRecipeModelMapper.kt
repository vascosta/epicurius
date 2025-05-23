package epicurius.repository.jdbi.mappers.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.MealType
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.jdbi.utils.getArray
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class JdbiRecipeModelMapper(
    private val intoleranceSetMapper: ColumnMapper<List<Intolerance>>,
    private val dietSetMapper: ColumnMapper<List<Diet>>,
    private val ingredientMapper: RowMapper<Ingredient>
) : RowMapper<JdbiRecipeModel> {

    override fun map(rs: ResultSet, ctx: StatementContext): JdbiRecipeModel {
        val cuisine = Cuisine.Companion.fromInt(rs.getInt("cuisine"))
        val mealType = MealType.Companion.fromInt(rs.getInt("meal_type"))
        val intolerances = intoleranceSetMapper.map(rs, 9, ctx)
        val diets = dietSetMapper.map(rs, 10, ctx)
        val dbPicturesArray = rs.getArray(15)
        val picturesNames = getArray<String>(dbPicturesArray).toList()

        val recipe = JdbiRecipeModel(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            authorId = rs.getInt("author_id"),
            authorUsername = rs.getString("author_username"),
            date = rs.getDate("date").toLocalDate(),
            servings = rs.getInt("servings"),
            preparationTime = rs.getInt("preparation_time"),
            cuisine = cuisine,
            mealType = mealType,
            intolerances = intolerances,
            diets = diets,
            ingredients = emptyList(),
            calories = rs.getObject("calories", Integer::class.java)?.toInt(),
            protein = rs.getObject("protein", Integer::class.java)?.toInt(),
            fat = rs.getObject("fat", Integer::class.java)?.toInt(),
            carbs = rs.getObject("carbs", Integer::class.java)?.toInt(),
            picturesNames = picturesNames
        )

        val ingredients = mutableListOf<Ingredient>()
        do {
            ingredients.add(ingredientMapper.map(rs, ctx))
        } while (rs.next())

        return recipe.copy(ingredients = ingredients)
    }
}
