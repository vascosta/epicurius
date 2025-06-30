package android.epicurius.ui.notifications

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleDailyProductCheck(context: Context) {
    val request = PeriodicWorkRequestBuilder<ExpirationCheckWorker>(
        1, TimeUnit.DAYS
    ).build()

    WorkManager.getInstance(context).enqueue(request)
}
