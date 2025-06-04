package android.epicurius.ui.screens.fridge

import android.epicurius.MainActivity
import android.epicurius.domain.fridge.Product
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import java.time.LocalDate

class FridgeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val sampleProducts = listOf(
                Product("Milk", 1, 2, LocalDate.now().minusDays(1), LocalDate.now().plusDays(5)),
                Product("Eggs", 2, 12, null, LocalDate.now().plusDays(10)),
                Product("Meat", 3, 1, LocalDate.now().minusDays(2), LocalDate.now().minusDays(1)),
                Product("Cheese", 4, 1, LocalDate.now().minusDays(3), LocalDate.now().plusDays(2)),
                Product("Yogurt", 5, 4, null, LocalDate.now().plusDays(1)),
            )
            FridgeScreen(
                products = sampleProducts,
                onBackButton = { navigateTo<MainActivity>() },
            )
        }
    }
}
