package epicurius.repository.jdbi.mappers

import epicurius.domain.Diet
import epicurius.repository.jdbi.utils.getArray
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class DietListMapper : ColumnMapper<List<Diet>> {
    override fun map(rs: ResultSet, columnNumber: Int, ctx: StatementContext): List<Diet> {
        val dbArray = rs.getArray(columnNumber)
        val intoleranceIdx = getArray<Int>(dbArray).toList()

        return intoleranceIdx.map { Diet.fromInt(it) }
    }
}
