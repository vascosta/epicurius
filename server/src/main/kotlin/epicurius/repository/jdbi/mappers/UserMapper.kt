package epicurius.repository.jdbi.mappers

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.user.User
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class UserMapper(
    private val intoleranceSetMapper: ColumnMapper<Set<Intolerance>>,
    private val dietSetMapper: ColumnMapper<Set<Diet>>
) : RowMapper<User> {
    override fun map(rs: ResultSet, ctx: StatementContext): User {
        val intoleranceSet = intoleranceSetMapper.map(rs, 8, ctx)
        val dietSet = dietSetMapper.map(rs, 9, ctx)
        return User(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            email = rs.getString("email"),
            passwordHash = rs.getString("password_hash"),
            tokenHash = rs.getString("token_hash"),
            country = rs.getString("country"),
            privacy = rs.getBoolean("privacy"),
            intolerances = intoleranceSet,
            diets = dietSet,
            profilePictureName = rs.getString("profile_picture_name")
        )
    }
}
