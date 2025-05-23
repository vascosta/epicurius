package epicurius.unit

import epicurius.EpicuriusTest
import epicurius.domain.fridge.FridgeDomain
import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.picture.PictureDomain
import epicurius.domain.user.CountriesDomain
import epicurius.domain.user.UserDomain
import epicurius.http.controllers.collection.CollectionController
import epicurius.http.controllers.feed.FeedController
import epicurius.http.controllers.fridge.FridgeController
import epicurius.http.controllers.ingredients.IngredientsController
import epicurius.http.controllers.mealPlanner.MealPlannerController
import epicurius.http.controllers.menu.MenuController
import epicurius.http.controllers.rateRecipe.RateRecipeController
import epicurius.http.controllers.recipe.RecipeController
import epicurius.http.controllers.user.UserController
import epicurius.repository.cloudFunction.CloudFunctionRepository
import epicurius.repository.cloudFunction.manager.CloudFunctionManager
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.cloudStorage.picture.PictureRepository
import epicurius.repository.firestore.manager.FirestoreManager
import epicurius.repository.firestore.recipe.FirestoreRecipeRepository
import epicurius.repository.jdbi.collection.JdbiCollectionRepository
import epicurius.repository.jdbi.feed.JdbiFeedRepository
import epicurius.repository.jdbi.fridge.JdbiFridgeRepository
import epicurius.repository.jdbi.mealPlanner.JdbiMealPlannerRepository
import epicurius.repository.jdbi.rateRecipe.JdbiRateRecipeRepository
import epicurius.repository.jdbi.recipe.JdbiRecipeRepository
import epicurius.repository.jdbi.token.JdbiTokenRepository
import epicurius.repository.jdbi.user.JdbiUserRepository
import epicurius.repository.spoonacular.SpoonacularRepository
import epicurius.repository.spoonacular.manager.SpoonacularManager
import epicurius.repository.transaction.Transaction
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import epicurius.services.collection.CollectionService
import epicurius.services.feed.FeedService
import epicurius.services.fridge.FridgeService
import epicurius.services.ingredients.IngredientsService
import epicurius.services.mealPlanner.MealPlannerService
import epicurius.services.menu.MenuService
import epicurius.services.rateRecipe.RateRecipeService
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
            userServiceMock,
            fridgeServiceMock,
            recipeServiceMock,
            rateRecipeServiceMock,
            feedServiceMock,
            menuServiceMock,
            ingredientsServiceMock,
            collectionServiceMock,
            mealPlannerServiceMock,
            jdbiUserRepositoryMock,
            jdbiTokenRepositoryMock,
            jdbiFridgeRepositoryMock,
            jdbiRecipeRepositoryMock,
            jdbiRateRecipeRepositoryMock,
            jdbiMealPlannerRepositoryMock,
            jdbiCollectionRepositoryMock,
            firestoreRecipeRepositoryMock,
            pictureRepositoryMock,
            spoonacularRepositoryMock,
            cloudFunctionRepositoryMock,
            userDomainMock,
            pictureDomainMock,
            countriesDomainMock,
            fridgeDomainMock,
            mealTimeMock
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
                    override val rateRecipeRepository = jdbiRateRecipeRepositoryMock
                    override val mealPlannerRepository = jdbiMealPlannerRepositoryMock
                    override val feedRepository = jdbiFeedRepositoryMock
                    override val collectionRepository = jdbiCollectionRepositoryMock
                })
            }
        }

        val jdbiUserRepositoryMock: JdbiUserRepository = mock()
        val jdbiTokenRepositoryMock: JdbiTokenRepository = mock()
        val jdbiFridgeRepositoryMock: JdbiFridgeRepository = mock()
        val jdbiRecipeRepositoryMock: JdbiRecipeRepository = mock()
        val jdbiRateRecipeRepositoryMock: JdbiRateRecipeRepository = mock()
        val jdbiMealPlannerRepositoryMock: JdbiMealPlannerRepository = mock()
        val jdbiFeedRepositoryMock: JdbiFeedRepository = mock()
        val jdbiCollectionRepositoryMock: JdbiCollectionRepository = mock()

        val firestoreRecipeRepositoryMock: FirestoreRecipeRepository = mock()
        val pictureRepositoryMock: PictureRepository = mock()
        val spoonacularRepositoryMock: SpoonacularRepository = mock()
        val cloudFunctionRepositoryMock: CloudFunctionRepository = mock()

        private val transactionManagerMock: JdbiTransactionManager = mock()
        private val firestoreManagerMock: FirestoreManager = mock<FirestoreManager>().apply {
            whenever(recipeRepository).thenReturn(firestoreRecipeRepositoryMock)
        }
        private val cloudStorageManagerMock = mock<CloudStorageManager>().apply {
            whenever(pictureRepository).thenReturn(pictureRepositoryMock)
        }
        private val spoonacularStorageManagerMock = mock<SpoonacularManager>().apply {
            whenever(spoonacularRepository).thenReturn(spoonacularRepositoryMock)
        }
        private val cloudFunctionManagerMock = mock<CloudFunctionManager>().apply {
            whenever(cloudFunctionRepository).thenReturn(cloudFunctionRepositoryMock)
        }

        val userDomainMock: UserDomain = mock()
        val pictureDomainMock: PictureDomain = mock()
        val countriesDomainMock: CountriesDomain = mock()
        val fridgeDomainMock: FridgeDomain = mock()
        val mealTimeMock: MealTime = mock()

        val userService = UserService(transactionManagerMock, cloudStorageManagerMock, userDomainMock, pictureDomainMock, countriesDomainMock)
        val feedService = FeedService(transactionManagerMock, cloudStorageManagerMock)
        val fridgeService = FridgeService(transactionManagerMock, spoonacularStorageManagerMock, fridgeDomainMock)
        val recipeService = RecipeService(
            transactionManagerMock, firestoreManagerMock, cloudStorageManagerMock, spoonacularStorageManagerMock, pictureDomainMock
        )
        val rateRecipeService = RateRecipeService(transactionManagerMock)
        val ingredientsService = IngredientsService(
            cloudStorageManagerMock,
            spoonacularStorageManagerMock,
            cloudFunctionManagerMock,
            pictureDomainMock
        )
        val menuService = MenuService(transactionManagerMock, cloudStorageManagerMock)
        val collectionService = CollectionService(transactionManagerMock, cloudStorageManagerMock)
        val mealPlannerService = MealPlannerService(transactionManagerMock, cloudStorageManagerMock)

        val userServiceMock: UserService = mock()
        val fridgeServiceMock: FridgeService = mock()
        val recipeServiceMock: RecipeService = mock()
        val rateRecipeServiceMock: RateRecipeService = mock()
        val feedServiceMock: FeedService = mock()
        val menuServiceMock: MenuService = mock()
        val ingredientsServiceMock: IngredientsService = mock()
        val collectionServiceMock: CollectionService = mock()
        val mealPlannerServiceMock: MealPlannerService = mock()

        val userController = UserController(userServiceMock)
        val fridgeController = FridgeController(fridgeServiceMock)
        val recipeController = RecipeController(recipeServiceMock)
        val rateRecipeController = RateRecipeController(rateRecipeServiceMock)
        val feedController = FeedController(feedServiceMock)
        val menuController = MenuController(menuServiceMock)
        val ingredientsController = IngredientsController(ingredientsServiceMock)
        val collectionController = CollectionController(collectionServiceMock)
        val mealPlannerController = MealPlannerController(mealPlannerServiceMock)
    }
}
