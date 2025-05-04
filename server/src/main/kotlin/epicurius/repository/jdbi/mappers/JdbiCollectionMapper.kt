package epicurius.repository.jdbi.mappers

import epicurius.domain.collection.CollectionType
import epicurius.repository.jdbi.collection.models.JdbiCollectionModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class JdbiCollectionMapper(private val recipeInfo: JdbiRecipeInfoMapper): RowMapper<JdbiCollectionModel> {

    override fun map(rs: ResultSet, ctx: StatementContext): JdbiCollectionModel? {
        val recipes = mutableListOf<JdbiRecipeInfo>()

        do {
            val recipe = recipeInfo.map(rs, ctx)
            recipes.add(recipe)
        } while (rs.next())

        return JdbiCollectionModel(
            id = rs.getInt("collection_id"),
            name = rs.getString("collection_name"),
            type = CollectionType.fromInt(rs.getInt("collection_type")),
            recipes = recipes
        )
    }
}