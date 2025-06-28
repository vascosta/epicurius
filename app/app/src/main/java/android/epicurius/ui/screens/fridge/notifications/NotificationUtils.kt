package android.epicurius.ui.screens.fridge.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.epicurius.R
import android.epicurius.domain.fridge.Product
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun createNotificationChannelIfNeeded(context: Context) {
    val channel = NotificationChannel(
        "fridge_channel",
        "Fridge Notifications",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Notifications for fridge products"
    }
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(channel)
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun notifyProductExpiration(context: Context, product: Product) {
    val notificationManager = NotificationManagerCompat.from(context)

    val today = LocalDate.now()
    val contentText = when (product.expirationDate) {
        today.minusDays(2) -> "${product.name} expired two days ago. Consider discarding it."
        today.minusDays(1) -> "${product.name} expired yesterday. Check if it's still usable."
        today -> "${product.name} expires today. Use it soon!"
        today.plusDays(1) -> "${product.name} expires tomorrow. Plan to use it!"
        today.plusDays(2) -> "${product.name} expires in two days. Don't forget to use it!"
        today.plusDays(8) -> "${product.name} expires in a week. Make sure to use it before then."
        else -> "${product.name} expired on " +
                "${product.expirationDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}. " +
                "Double check before consuming."
    }

    val notification = NotificationCompat.Builder(context, "fridge_channel")
        .setSmallIcon(R.drawable.fridge)
        .setContentTitle("Fridge Alert")
        .setContentText(contentText)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(Notification.DEFAULT_ALL)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(product.entryNumber, notification)
}
