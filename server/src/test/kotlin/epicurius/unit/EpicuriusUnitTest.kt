package epicurius.unit

import epicurius.EpicuriusTest
import epicurius.domain.PictureDomain
import epicurius.domain.user.CountriesDomain
import epicurius.domain.user.UserDomain
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.jdbi.fridge.JdbiFridgeRepository
import epicurius.repository.jdbi.mealPlanner.JdbiMealPlannerRepository
import epicurius.repository.jdbi.recipe.JdbiRecipeRepository
import epicurius.repository.jdbi.user.JdbiTokenRepository
import epicurius.repository.jdbi.user.JdbiUserRepository
import epicurius.repository.transaction.Transaction
import epicurius.repository.transaction.jdbi.JdbiTransaction
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import epicurius.services.FridgeService
import epicurius.services.UserService
import org.junit.jupiter.api.BeforeAll
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

open class EpicuriusUnitTest: EpicuriusTest() {

    companion object {

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val mockTransaction: JdbiTransaction = mock<JdbiTransaction>().apply {
                whenever(userRepository).thenReturn(userRepositoryMock)
                whenever(tokenRepository).thenReturn(tokenRepositoryMock)
                whenever(fridgeRepository).thenReturn(fridgeRepositoryMock)
                whenever(recipeRepository).thenReturn(recipeRepositoryMock)
                whenever(mealPlannerRepository).thenReturn(mealPlannerRepositoryMock)
            }

            whenever(transactionManagerMock.run<Any>(any())).thenAnswer { invocation ->
                val block = invocation.getArgument<Function1<Transaction, Any>>(0)
                block(mockTransaction)
            }
        }
        val userRepositoryMock: JdbiUserRepository = mock()
        val tokenRepositoryMock: JdbiTokenRepository = mock()
        val fridgeRepositoryMock: JdbiFridgeRepository = mock()
        val recipeRepositoryMock: JdbiRecipeRepository = mock()
        val mealPlannerRepositoryMock: JdbiMealPlannerRepository = mock()

        private val transactionManagerMock: JdbiTransactionManager = mock()
        private val cloudStorageManagerMock = mock<CloudStorageManager>()
        val usersDomainMock: UserDomain = mock()
        val pictureDomainMock: PictureDomain = mock()
        val countriesDomainMock: CountriesDomain = mock()

        val userService = UserService(tm, cs, usersDomain, pictureDomain, countriesDomain)
        val fridgeService = FridgeService(tm, sm, fridgeDomain)
    }
}