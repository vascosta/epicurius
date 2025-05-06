package epicurius.repository.jdbi.collection

import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionNotFound
import epicurius.repository.jdbi.collection.contract.CollectionRepository
import epicurius.repository.jdbi.collection.models.JdbiCollectionModel
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiCollectionRepository(private val handle: Handle) : CollectionRepository {

    override fun createCollection(ownerId: Int, collectionName: String, collectionType: CollectionType): Int =
        handle.createUpdate(
            """
                INSERT INTO dbo.collection (owner_id, name, type)
                VALUES (:ownerId, :name, :type)
                RETURNING id
            """
        )
            .bind("ownerId", ownerId)
            .bind("name", collectionName)
            .bind("type", collectionType.ordinal)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getCollection(ownerId: Int, collectionName: String, collectionType: CollectionType) =
        handle.createQuery(
            """
                SELECT c.id as collection_id, c.owner_id as owner_id, c.name as collection_name, c.type as collection_type,
                r.id AS recipe_id, r.name AS recipe_name,
                r.cuisine, r.meal_type, r.preparation_time, r.servings, r.pictures_names
                FROM dbo.collection c
                LEFT JOIN dbo.collection_recipe cr ON cr.collection_id = c.id
                LEFT JOIN dbo.recipe r ON r.id = cr.recipe_id
                WHERE c.owner_id = :ownerId AND c.name = :collectionName AND c.type = :collectionType
            """
        )
            .bind("ownerId", ownerId)
            .bind("collectionName", collectionName)
            .bind("collectionType", collectionType.ordinal)
            .mapTo<JdbiCollectionModel>()
            .firstOrNull()

    override fun getCollectionById(collectionId: Int): JdbiCollectionModel? {
        val query = StringBuilder()
        applyGetJdbiCollectionModelByIdQuery(query)

        return handle.createQuery(query)
            .bind("collectionId", collectionId)
            .mapTo<JdbiCollectionModel>()
            .firstOrNull()
    }

    override fun updateCollection(collectionId: Int, newName: String?): JdbiCollectionModel {
        val query = StringBuilder(
            """
                WITH updated_collection AS (
                    UPDATE dbo.collection
                    SET name = COALESCE(:name, name)
                    WHERE id = :collectionId
                    RETURNING *
                )
            """
        )
        applyGetJdbiCollectionModelByIdQuery(query, "updated_collection")

        return handle.createQuery(query)
            .bind("collectionId", collectionId)
            .bind("name", newName)
            .mapTo<JdbiCollectionModel>()
            .first()
    }

    override fun addRecipeToCollection(collectionId: Int, recipeId: Int): JdbiCollectionModel {
        handle.createUpdate(
            """
                INSERT INTO dbo.collection_recipe (collection_id, recipe_id)
                VALUES (:collectionId, :recipeId)
            """
        )
            .bind("collectionId", collectionId)
            .bind("recipeId", recipeId)
            .execute()
        return getCollectionById(collectionId) ?: throw CollectionNotFound()
    }

    override fun removeRecipeFromCollection(collectionId: Int, recipeId: Int): JdbiCollectionModel {
        handle.createUpdate(
            """
               DELETE FROM dbo.collection_recipe
               WHERE collection_id = :collectionId AND recipe_id = :recipeId
            """
        )
            .bind("collectionId", collectionId)
            .bind("recipeId", recipeId)
            .execute()
        return getCollectionById(collectionId) ?: throw CollectionNotFound()
    }

    override fun deleteCollection(collectionId: Int) {
        handle.createUpdate(
            """
                DELETE FROM dbo.collection
                WHERE id = :collectionId
            """
        )
            .bind("collectionId", collectionId)
            .execute()
    }

    private fun applyGetJdbiCollectionModelByIdQuery(query: StringBuilder, collection: String? = "dbo.collection") {
        query.append(
            """
                SELECT c.id as collection_id, c.owner_id as owner_id, c.name as collection_name, c.type as collection_type,
                r.id AS recipe_id, r.name AS recipe_name,
                r.cuisine, r.meal_type, r.preparation_time, r.servings, r.pictures_names
                FROM $collection c
                LEFT JOIN dbo.collection_recipe cr ON cr.collection_id = c.id
                LEFT JOIN dbo.recipe r ON r.id = cr.recipe_id
                WHERE c.id = :collectionId
            """
        )
    }
}
