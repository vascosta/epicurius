package epicurius.repository.mappers

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.user.User
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class UserMapper(
    private val intoleranceListMapper: ColumnMapper<List<Intolerance>>,
    private val dietListMapper: ColumnMapper<List<Diet>>
) : RowMapper<User> {
    override fun map(rs: ResultSet, ctx: StatementContext): User {
        val intoleranceList = intoleranceListMapper.map(rs, 8, ctx)
        val dietList = dietListMapper.map(rs, 9, ctx)
        return User(
            id = rs.getInt("id"),
            username = rs.getString("username"),
            email = rs.getString("email"),
            passwordHash = rs.getString("password_hash"),
            tokenHash = rs.getString("token_hash"),
            country = rs.getString("country"),
            privacy = rs.getBoolean("privacy"),
            intolerances = intoleranceList,
            diet = dietList,
            profilePictureName = rs.getString("profile_picture_name")
        )
    }
}
