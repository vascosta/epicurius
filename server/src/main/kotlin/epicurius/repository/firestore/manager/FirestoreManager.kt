package epicurius.repository.firestore.manager

import com.google.cloud.firestore.Firestore
import epicurius.repository.firestore.recipe.FirestoreRecipeRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
class FirestoreManager(firestore: Firestore) {
    val recipeRepository = FirestoreRecipeRepository(firestore)
}
