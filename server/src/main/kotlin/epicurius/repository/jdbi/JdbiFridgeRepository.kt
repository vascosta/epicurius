package epicurius.repository.jdbi

import epicurius.domain.fridge.Fridge
import epicurius.domain.fridge.Product
import epicurius.domain.fridge.ProductInfo
import epicurius.domain.fridge.UpdateProductInfo
import epicurius.repository.FridgePostgresRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiFridgeRepository(private val handle: Handle) : FridgePostgresRepository {
    override fun getFridge(userId: Int): Fridge {
        val fridgeProducts = handle.createQuery(
            """
                SELECT product_name, entry_number, quantity, open_date, expiration_date
                FROM dbo.fridge
                WHERE owner_id = :id
            """
        )
            .bind("id", userId)
            .mapTo<Product>()
            .list()

        return Fridge(fridgeProducts)
    }

    override fun addProduct(userId: Int, product: ProductInfo): Fridge {
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

        return getFridge(userId)
    }

    override fun updateProduct(userId: Int, product: UpdateProductInfo): Fridge {
        handle.createUpdate(
            """
                UPDATE dbo.fridge
                SET quantity = COALESCE(:quantity, quantity),
                    open_date = COALESCE(:openDate, open_date),
                    expiration_date = COALESCE(:expirationDate, expiration_date)
                WHERE owner_id = :id AND entry_number = :number
            """
        )
            .bind("quantity", product.quantity)
            .bind("openDate", product.openDate)
            .bind("expirationDate", product.expirationDate)
            .bind("id", userId)
            .bind("number", product.entryNumber)
            .execute()

        return getFridge(userId)
    }

    override fun removeProduct(userId: Int, entryNumber: Int): Fridge {
        handle.createUpdate(
            """
                DELETE FROM dbo.fridge
                WHERE owner_id = :id AND entry_number = :number
            """
        )
            .bind("id", userId)
            .bind("number", entryNumber)
            .execute()

        return getFridge(userId)
    }

    override fun checkIfProductExistsInFridge(userId: Int, entryNumber: Int?, product: ProductInfo?): Product? {
        val query = StringBuilder(
            """
                SELECT product_name, entry_number, quantity, open_date, expiration_date
                FROM dbo.fridge 
                WHERE owner_id = :id
            """
        )

        if (product?.productName != null) query.append(" AND product_name = :name")
        if (product?.openDate != null) query.append(" AND open_date = :open")
        if (product?.expirationDate != null) query.append(" AND expiration_date = :date")
        if (entryNumber != null) query.append(" AND entry_number = :number")

        val result = handle.createQuery(query.toString())
            .bind("id", userId)

        product?.productName?.let { result.bind("name", it) }
        product?.openDate?.let { result.bind("open", it) }
        product?.expirationDate?.let { result.bind("date", it) }
        entryNumber?.let { result.bind("number", it) }

        return result.mapTo<Product>().firstOrNull()
    }

    override fun checkIfProductIsOpen(userId: Int, entryNumber: Int): Boolean =
        handle.createQuery(
            """
                SELECT COUNT(*) FROM dbo.fridge 
                WHERE owner_id = :id AND entry_number = :number AND open_date IS NULL
            """
        )
            .bind("id", userId)
            .bind("number", entryNumber)
            .mapTo<Int>()
            .one() == 0
}
