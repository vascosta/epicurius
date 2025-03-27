package epicurius.repository.jdbi

import epicurius.domain.fridge.Fridge
import epicurius.domain.fridge.Product
import epicurius.repository.FridgePostgresRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiFridgeRepository(private val handle: Handle) : FridgePostgresRepository {
    override fun getFridge(userId: Int): Fridge {
        val fridgeProducts = handle.createQuery(
            """
                SELECT *
                FROM dbo.fridge
                WHERE owner_id = :id
            """
        )
            .bind("id", userId)
            .mapTo<Product>()
            .list()

        return Fridge(fridgeProducts)
    }

    override fun addProduct(userId: Int, product: Product): Fridge {
        handle.createUpdate(
            """
                INSERT INTO dbo.fridge(owner_id, product_name, quantity, open_date, expiration_date)
                VALUES (:id, :name, :quantity, :openDate, :expirationDate)
            """
        )
            .bind("id", userId)
            .bind("name", product.productName)
            .bind("quantity", product.quantity)
            .bind("openDate", product.openDate)
            .bind("expirationDate", product.expirationDate)
            .execute()

        val fridgeProducts = handle.createQuery(
            """
                SELECT *
                FROM dbo.fridge
                WHERE owner_id = :id
            """
        )
            .bind("id", userId)
            .mapTo<Product>()
            .list()

        return Fridge(fridgeProducts)
    }
}
