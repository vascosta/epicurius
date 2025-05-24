package epicurius.repository.jdbi.mappers.collection

import epicurius.domain.collection.CollectionType
import epicurius.repository.jdbi.collection.models.JdbiCollectionModel
import epicurius.repository.jdbi.mappers.recipe.JdbiRecipeInfoMapper
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class JdbiCollectionMapper(private val recipeInfo: JdbiRecipeInfoMapper) : RowMapper<JdbiCollectionModel> {

    override fun map(rs: ResultSet, ctx: StatementContext): JdbiCollectionModel? {
        val recipes = mutableListOf<JdbiRecipeInfo>()

        val jdbiCollectionModel = JdbiCollectionModel(
            id = rs.getInt("collection_id"),
            ownerId = rs.getInt("owner_id"),
            name = rs.getString("collection_name"),
            type = CollectionType.Companion.fromInt(rs.getInt("collection_type")),
            recipes = emptyList()
        )

        if (rs.getInt("recipe_id") != 0) {
            do {
                val recipe = recipeInfo.map(rs, ctx)
                recipes.add(recipe)
            } while (rs.next())
        }

        return jdbiCollectionModel.copy(recipes = recipes)
    }
}