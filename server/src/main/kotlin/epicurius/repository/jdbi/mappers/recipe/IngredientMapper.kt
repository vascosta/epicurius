package epicurius.repository.jdbi.mappers.recipe

import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.IngredientUnit.Companion.fromInt
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class IngredientMapper : RowMapper<Ingredient> {
    override fun map(rs: ResultSet, ctx: StatementContext): Ingredient {
        val unit = IngredientUnit.Companion.fromInt(rs.getInt("unit"))

        return Ingredient(
            name = rs.getString("ingredient_name"),
            quantity = rs.getDouble("quantity"),
            unit = unit
        )
    }
}