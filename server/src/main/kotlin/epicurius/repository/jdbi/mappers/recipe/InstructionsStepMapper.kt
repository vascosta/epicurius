package epicurius.repository.jdbi.mappers.recipe

import epicurius.domain.recipe.Step
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class InstructionsStepMapper : RowMapper<Step> {
    override fun map(rs: ResultSet, ctx: StatementContext): Step {
        return Pair(
            rs.getString("step_number"),
            rs.getString("step_description")
        )
    }
}