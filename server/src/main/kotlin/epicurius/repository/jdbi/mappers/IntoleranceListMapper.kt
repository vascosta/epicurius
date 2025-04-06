package epicurius.repository.jdbi.mappers

import epicurius.domain.Intolerance
import epicurius.repository.jdbi.utils.getArray
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class IntoleranceListMapper : ColumnMapper<List<Intolerance>> {
    override fun map(rs: ResultSet, columnNumber: Int, ctx: StatementContext): List<Intolerance> {
        val dbArray = rs.getArray(columnNumber)
        val intoleranceIdx = getArray<Int>(dbArray).toList()

        return intoleranceIdx.map { Intolerance.fromInt(it) }
    }
}
