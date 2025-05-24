package epicurius.repository.jdbi.mappers.collection

import epicurius.repository.jdbi.collection.models.JdbiCollectionProfileModel
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class JdbiCollectionProfileMapper: RowMapper<JdbiCollectionProfileModel> {

    override fun map(rs: ResultSet, ctx: StatementContext): JdbiCollectionProfileModel? {
        return JdbiCollectionProfileModel(rs.getInt("collection_id"), rs.getString("collection_name"))
    }
}