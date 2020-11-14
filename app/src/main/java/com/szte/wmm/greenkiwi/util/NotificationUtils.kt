package com.szte.wmm.greenkiwi.util

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.szte.wmm.greenkiwi.MainActivity
import com.szte.wmm.greenkiwi.R

/**
 * Builds and sends a notification related to the app.
 */
fun NotificationManager.sendNotification(notificationId: Int, notificationChannelId: String, text: String, bigText: String, applicationContext: Context) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationId,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val petImage = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.kiwi_green)

    val notificationBuilder = NotificationCompat.Builder(applicationContext, notificationChannelId)
        .setSmallIcon(R.drawable.kiwi_green)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(text)
        .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
        .setLargeIcon(petImage)
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setAutoCancel(true)

    notify(notificationId, notificationBuilder.build())
}

/**
 * Cancel all notifications.
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}

/**
 * Creates a notification channel related to the app.
 */
fun createNotificationChannel(app: Application, channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Notifications of the kiwi's hunger"

        val notificationManager = app.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}
