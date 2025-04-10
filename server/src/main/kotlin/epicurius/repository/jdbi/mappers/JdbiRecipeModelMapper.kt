package epicurius.repository.jdbi.mappers

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Cuisine.Companion.fromInt
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.MealType.Companion.fromInt
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.jdbi.utils.getArray
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class JdbiRecipeModelMapper(
    private val intoleranceListMapper: ColumnMapper<List<Intolerance>>,
    private val dietListMapper: ColumnMapper<List<Diet>>,
    private val ingredientMapper: RowMapper<Ingredient>
) : RowMapper<JdbiRecipeModel> {

    override fun map(rs: ResultSet, ctx: StatementContext): JdbiRecipeModel {
        val cuisine = Cuisine.fromInt(rs.getInt("cuisine"))
        val mealType = MealType.fromInt(rs.getInt("meal_type"))
        val intolerances = intoleranceListMapper.map(rs, 9, ctx)
        val diets = dietListMapper.map(rs, 10, ctx)
        val dbPicturesArray = rs.getArray(15)
        val picturesNames = getArray<String>(dbPicturesArray).toList()

        val recipe = JdbiRecipeModel(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            authorId = rs.getInt("author_id"),
            authorUsername = rs.getString("username"),
            date = rs.getDate("date"),
            servings = rs.getInt("servings"),
            preparationTime = rs.getInt("preparation_time"),
            cuisine = cuisine,
            mealType = mealType,
            intolerances = intolerances,
            diets = diets,
            ingredients = emptyList(),
            calories = rs.getInt("calories"),
            protein = rs.getInt("protein"),
            fat = rs.getInt("fat"),
            carbs = rs.getInt("carbs"),
            picturesNames = picturesNames
        )

        val ingredients = mutableListOf<Ingredient>()
        do {
            ingredients.add(ingredientMapper.map(rs, ctx))
        } while (rs.next())

        return recipe.copy(ingredients = ingredients)
    }
}
