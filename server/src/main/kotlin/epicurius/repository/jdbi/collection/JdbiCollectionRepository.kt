package epicurius.repository.jdbi.collection

import epicurius.domain.collection.CollectionType
import epicurius.http.collection.models.input.CreateCollectionInputModel
import epicurius.repository.jdbi.collection.contract.CollectionRepository
import epicurius.repository.jdbi.collection.models.JdbiCollectionModel
import epicurius.repository.jdbi.collection.models.JdbiUpdateCollectionModel
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiCollectionRepository(private val handle: Handle): CollectionRepository {

    override fun createCollection(ownerId: Int, createCollectionInfo: CreateCollectionInputModel): Int =
        handle.createUpdate(
            """
                INSERT INTO dbo.collection (owner_id, name, type)
                VALUES (:ownerId, :name, :type)
                RETURNING id
            """
        )
            .bind("ownerId", ownerId)
            .bind("name", createCollectionInfo.name)
            .bind("type", createCollectionInfo.type.ordinal)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getCollection(ownerId: Int, collectionName: String, collectionType: CollectionType) =
        handle.createQuery(
            """
                SELECT c.id as collection_id, c.name as collection_name, c.type, 
                r.id AS recipe_id, r.name AS recipe_name
                r.cuisine, r.meal_type, r.preparation_time, r.servings, r.pictures_names
                FROM dbo.collection c
                JOIN dbo.collection_recipe cr ON cr.collection_id = c.id
                JOIN dbo.recipes r ON r.id = cr.recipe_id
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
        applyGetJdbiCollectionModelQuery(query)

        return handle.createQuery(query)
            .bind("collectionId", collectionId)
            .mapTo<JdbiCollectionModel>()
            .firstOrNull()
    }

    override fun updateCollection(collectionId: Int, updateCollectionInfo: JdbiUpdateCollectionModel): JdbiCollectionModel  {
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
        applyGetJdbiCollectionModelQuery(query)

        return handle.createQuery(query)
            .bind("collectionId", collectionId)
            .bind("name", updateCollectionInfo.name)
            .mapTo<JdbiCollectionModel>()
            .first()
    }

    override fun addRecipeToCollection(collectionId: Int, recipeId: Int): JdbiCollectionModel {
        val query = StringBuilder(
            """
                WITH new_recipe as (
                    INSERT INTO dbo.collection_recipe (collection_id, recipe_id)
                    VALUES (:collectionId, :recipeId)
                )
            """
        )
        applyGetJdbiCollectionModelQuery(query)

        return handle.createQuery(query)
            .bind("collectionId", collectionId)
            .bind("recipeId", recipeId)
            .mapTo<JdbiCollectionModel>()
            .first()
    }

    override fun removeRecipeFromCollection(collectionId: Int, recipeId: Int): JdbiCollectionModel {
        val query = StringBuilder(
            """
                WITH recipe_to_remove AS (
                    DELETE FROM dbo.collection_recipe
                    WHERE collection_id = :collectionId AND recipe_id = :recipeId
                )
            """
        )
        applyGetJdbiCollectionModelQuery(query)

        return handle.createQuery(query)
            .bind("collectionId", collectionId)
            .bind("recipeId", recipeId)
            .mapTo<JdbiCollectionModel>()
            .first()
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

    private fun applyGetJdbiCollectionModelQuery(query: StringBuilder) {
        query.append(
            """
                SELECT c.id as collection_id, c.name as collection_name, c.type, 
                r.id AS recipe_id, r.name AS recipe_name
                r.cuisine, r.meal_type, r.preparation_time, r.servings, r.pictures_names
                FROM dbo.collection c
                JOIN dbo.collection_recipe cr ON cr.collection_id = c.id
                JOIN dbo.recipes r ON r.id = cr.recipe_id
                WHERE c.collection_id = :collectionId
            """
        )
    }
}