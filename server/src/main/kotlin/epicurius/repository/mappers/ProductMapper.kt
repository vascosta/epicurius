package epicurius.repository.mappers

import epicurius.domain.fridge.Product
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class ProductMapper : RowMapper<Product> {
    override fun map(rs: ResultSet, ctx: StatementContext): Product {
        return Product(
            productName = rs.getString("product_name"),
            quantity = rs.getInt("quantity"),
            openDate = rs.getDate("open_date"),
            expirationDate = rs.getDate("expiration_date")
        )
    }
}
