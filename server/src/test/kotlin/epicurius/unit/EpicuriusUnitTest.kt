package epicurius.unit

import epicurius.EpicuriusTest
import epicurius.domain.fridge.FridgeDomain
import epicurius.domain.picture.PictureDomain
import epicurius.domain.user.CountriesDomain
import epicurius.domain.user.UserDomain
import epicurius.http.fridge.FridgeController
import epicurius.http.recipe.RecipeController
import epicurius.repository.cloudFunction.manager.CloudFunctionManager
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.cloudStorage.picture.CloudStoragePictureRepository
import epicurius.repository.firestore.FirestoreManager
import epicurius.repository.firestore.recipe.FirestoreRecipeRepository
import epicurius.repository.jdbi.fridge.JdbiFridgeRepository
import epicurius.repository.jdbi.mealPlanner.JdbiMealPlannerRepository
import epicurius.repository.jdbi.recipe.JdbiRecipeRepository
import epicurius.repository.jdbi.token.JdbiTokenRepository
import epicurius.repository.jdbi.user.JdbiUserRepository
import epicurius.repository.spoonacular.SpoonacularRepository
import epicurius.repository.spoonacular.manager.SpoonacularManager
import epicurius.repository.transaction.Transaction
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import epicurius.services.fridge.FridgeService
import epicurius.services.recipe.RecipeService
import epicurius.services.user.UserService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever

open class EpicuriusUnitTest : EpicuriusTest() {

    @BeforeEach
    fun resetMocks() {
        reset(
            jdbiUserRepositoryMock,
            jdbiTokenRepositoryMock,
            jdbiFridgeRepositoryMock,
            jdbiRecipeRepositoryMock,
            jdbiMealPlannerRepositoryMock,
            firestoreRecipeRepositoryMock,
            pictureRepositoryMock,
            spoonacularRepositoryMock,
            userDomainMock,
            pictureDomainMock,
            countriesDomainMock,
            fridgeDomainMock,
        )
    }

    companion object {

        @JvmStatic
        @BeforeAll
        fun setUp() {
            whenever(transactionManagerMock.run<Any>(any())).thenAnswer { invocation ->
                val block = invocation.getArgument<Function1<Transaction, Any>>(0)
                block(object : Transaction {
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

        val pictureRepositoryMock: CloudStoragePictureRepository = mock()

        val spoonacularRepositoryMock: SpoonacularRepository = mock()

        private val transactionManagerMock: JdbiTransactionManager = mock()
        private val firestoreManagerMock: FirestoreManager = mock<FirestoreManager>().apply {
            whenever(recipeRepository).thenReturn(firestoreRecipeRepositoryMock)
        }
        private val cloudStorageManagerMock = mock<CloudStorageManager>().apply {
            whenever(pictureRepository).thenReturn(pictureRepositoryMock)
        }
        private val spoonacularStorageManagerMock: SpoonacularManager = mock<SpoonacularManager>().apply {
            whenever(spoonacularRepository).thenReturn(spoonacularRepositoryMock)
        }
        private val cloudFunctionManager: CloudFunctionManager = mock()

        val userDomainMock: UserDomain = mock()
        val pictureDomainMock: PictureDomain = mock()
        val countriesDomainMock: CountriesDomain = mock()
        val fridgeDomainMock: FridgeDomain = mock()

        val userService = UserService(transactionManagerMock, cloudStorageManagerMock, userDomainMock, pictureDomainMock, countriesDomainMock)
        val fridgeService = FridgeService(transactionManagerMock, spoonacularStorageManagerMock, fridgeDomainMock)
        val recipeService = RecipeService(
            transactionManagerMock, firestoreManagerMock, cloudStorageManagerMock, spoonacularStorageManagerMock, cloudFunctionManager, pictureDomainMock
        )

        val fridgeServiceMock: FridgeService = mock()
        val recipeServiceMock: RecipeService = mock()

        val fridgeController = FridgeController(fridgeServiceMock)
        val recipeController = RecipeController(recipeServiceMock)
    }
}
