package epicurius.repository.jdbi

import epicurius.domain.fridge.Fridge
import epicurius.repository.FridgePostgresRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiFridgeRepository(private val handle: Handle) : FridgePostgresRepository {
    override fun getFridge(userId: Int): Fridge? {
        return handle.createQuery(
            """
                SELECT *
                FROM dbo.fridge
                WHERE owner_id = :id
            """
        )
            .bind("id", userId)
            .mapTo<Fridge>()
            .firstOrNull()
    }
}
