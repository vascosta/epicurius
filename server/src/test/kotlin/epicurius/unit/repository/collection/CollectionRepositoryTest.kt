package epicurius.unit.repository.collection

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.collection.CollectionType
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.MealType
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestCollection
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser

open class CollectionRepositoryTest : RepositoryTest() {

    companion object {
        val testOwner = createTestUser(tm)
        val testCollectionId = createTestCollection(tm, testOwner.id, CollectionType.FAVOURITE)

        fun createCollection(ownerId: Int, collectionName: String, collectionType: CollectionType) =
            tm.run { it.collectionRepository.createCollection(ownerId, collectionName, collectionType) }

        fun getCollection(ownerId: Int, collectionName: String, collectionType: CollectionType) =
            tm.run { it.collectionRepository.getCollection(ownerId, collectionName, collectionType) }

        fun getCollectionById(collectionId: Int) =
            tm.run { it.collectionRepository.getCollectionById(collectionId) }

        fun updateCollection(collectionId: Int, newName: String?) =
            tm.run { it.collectionRepository.updateCollection(collectionId, newName) }

        fun addRecipeToCollection(collectionId: Int, recipeId: Int) =
            tm.run { it.collectionRepository.addRecipeToCollection(collectionId, recipeId) }

        fun removeRecipeFromCollection(collectionId: Int, recipeId: Int) =
            tm.run { it.collectionRepository.removeRecipeFromCollection(collectionId, recipeId) }

        fun deleteCollection(collectionId: Int) = tm.run { it.collectionRepository.deleteCollection(collectionId) }

        fun checkIfUserIsCollectionOwner(collectionId: Int, userId: Int) =
            tm.run { it.collectionRepository.checkIfUserIsCollectionOwner(collectionId, userId) }
    }
}
