package epicurius.repository.transaction.firestore

import com.google.cloud.firestore.Firestore
import epicurius.repository.firestore.FirestoreUserRepository
import org.springframework.stereotype.Component

@Component
class FirestoreManager(firestore: Firestore) {
    val userRepository = FirestoreUserRepository(firestore)
}