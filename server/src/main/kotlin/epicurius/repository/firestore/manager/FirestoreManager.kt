package epicurius.repository.firestore.manager

import com.google.cloud.firestore.Firestore
import epicurius.repository.firestore.recipe.FirestoreRecipeRepository
import org.springframework.stereotype.Component

@Component
class FirestoreManager(firestore: Firestore) {
    val recipeRepository = FirestoreRecipeRepository(firestore)
}