package epicurius.repository.jdbi.mappers.mealPlanner

import epicurius.domain.fridge.ProductInfo
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class ProductInfoMapper : RowMapper<ProductInfo> {
    override fun map(rs: ResultSet, ctx: StatementContext): ProductInfo {
        return ProductInfo(
            productName = rs.getString("product_name"),
            quantity = rs.getInt("quantity"),
            openDate = rs.getDate("open_date").toLocalDate(),
            expirationDate = rs.getDate("expiration_date").toLocalDate()
        )
    }
}