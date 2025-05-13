package epicurius.repository.jdbi.mappers

import epicurius.domain.fridge.Product
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class ProductMapper : RowMapper<Product> {
    override fun map(rs: ResultSet, ctx: StatementContext): Product {
        return Product(
            name = rs.getString("product_name"),
            entryNumber = rs.getInt("entry_number"),
            quantity = rs.getInt("quantity"),
            openDate = rs.getDate("open_date")?.toLocalDate(),
            expirationDate = rs.getDate("expiration_date").toLocalDate()
        )
    }
}
