package epicurius.unit

import epicurius.EpicuriusTest
import epicurius.domain.PictureDomain
import epicurius.domain.fridge.FridgeDomain
import epicurius.domain.user.CountriesDomain
import epicurius.domain.user.UserDomain
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.cloudStorage.picture.PictureCloudStorageRepository
import epicurius.repository.firestore.FirestoreManager
import epicurius.repository.firestore.recipe.FirestoreRecipeRepository
import epicurius.repository.jdbi.fridge.FridgeRepository
import epicurius.repository.jdbi.fridge.JdbiFridgeRepository
import epicurius.repository.jdbi.mealPlanner.JdbiMealPlannerRepository
import epicurius.repository.jdbi.mealPlanner.MealPlannerRepository
import epicurius.repository.jdbi.recipe.JdbiRecipeRepository
import epicurius.repository.jdbi.recipe.RecipeRepository
import epicurius.repository.jdbi.user.JdbiTokenRepository
import epicurius.repository.jdbi.user.JdbiUserRepository
import epicurius.repository.jdbi.user.TokenRepository
import epicurius.repository.jdbi.user.UserRepository
import epicurius.repository.spoonacular.SpoonacularManager
import epicurius.repository.transaction.Transaction
import epicurius.repository.transaction.jdbi.JdbiTransaction
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import epicurius.services.FridgeService
import epicurius.services.user.UserService
import epicurius.services.recipe.RecipeService
import org.junit.jupiter.api.BeforeAll
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

open class EpicuriusUnitTest: EpicuriusTest() {

    companion object {

        @JvmStatic
        @BeforeAll
        fun setUp() {
            whenever(transactionManagerMock.run<Any>(any())).thenAnswer { invocation ->
                val block = invocation.getArgument<Function1<Transaction, Any>>(0)
                block(object: Transaction {
                    override val userRepository = jdbiUserRepositoryMock
                    override val tokenRepository = jdbiTokenRepositoryMock
                    override val fridgeRepository = jdbiFridgeRepositoryMock
                    override val recipeRepository = jdbiRecipeRepositoryMock
                    override val mealPlannerRepository = jdbiMealPlannerRepositoryMock
                })
            }
        }

        val jdbiUserRepositoryMock: JdbiUserRepository = mock()
        val jdbiTokenRepositoryMock: JdbiTokenRepository = mock()
        val jdbiFridgeRepositoryMock: JdbiFridgeRepository = mock()
        val jdbiRecipeRepositoryMock: JdbiRecipeRepository = mock()
        val jdbiMealPlannerRepositoryMock: JdbiMealPlannerRepository = mock()

        val firestoreRecipeRepositoryMock: FirestoreRecipeRepository = mock()

        val cloudStoragePictureRepositoryMock: PictureCloudStorageRepository = mock()

        val usersDomainMock: UserDomain = mock()
        val pictureDomainMock: PictureDomain = mock()
        val countriesDomainMock: CountriesDomain = mock()
        val fridgeDomainMock: FridgeDomain = mock()

        private val transactionManagerMock: JdbiTransactionManager = mock()
        private val firestoreManagerMock: FirestoreManager = mock<FirestoreManager>().apply {
            whenever(recipeRepository).thenReturn(firestoreRecipeRepositoryMock)
        }
        private val cloudStorageManagerMock = mock<CloudStorageManager>().apply {
            whenever(pictureCloudStorageRepository).thenReturn(cloudStoragePictureRepositoryMock)
        }
        private val spoonacularStorageManagerMock: SpoonacularManager = mock()

        // change to mocks when all service tests are mocked
        val userService = UserService(transactionManagerMock, cloudStorageManagerMock, usersDomainMock, pictureDomainMock, countriesDomainMock)
        val fridgeService = FridgeService(transactionManagerMock, spoonacularStorageManagerMock, fridgeDomainMock)
        val recipeService = RecipeService(transactionManagerMock, firestoreManagerMock, cloudStorageManagerMock, pictureDomainMock)
    }
}
