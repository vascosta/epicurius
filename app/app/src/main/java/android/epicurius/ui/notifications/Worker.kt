package android.epicurius.ui.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.epicurius.EpicuriusApplication
import android.epicurius.ui.notifications.repository.ProductRepository
import android.epicurius.ui.notifications.utils.createFridgeNotificationChannel
import android.epicurius.ui.notifications.utils.notifyProductExpiration
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.time.LocalDate

class ExpirationCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        createFridgeNotificationChannel(context)

        val app = context.applicationContext as EpicuriusApplication
        val session = app.session
        val fridgeService = app.service.fridgeService

        val products = ProductRepository(session, fridgeService).getUserFridgeProducts()

        val today = LocalDate.now()
        val targetDates = listOf(
            today.minusDays(2),
            today.minusDays(1),
            today,
            today.plusDays(1),
            today.plusDays(2),
            today.plusDays(8)
        )

        val isNotificationPermissionGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true // older versions do not require runtime permission
            }

        products.forEach { product ->
            if (product.expirationDate in targetDates || product.expirationDate < today) {
                if (isNotificationPermissionGranted) {
                    notifyProductExpiration(context, product)
                }
            }
        }

        return Result.success()
    }
}
