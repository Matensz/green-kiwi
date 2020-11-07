package com.szte.wmm.greenkiwi.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.szte.wmm.greenkiwi.MainActivity
import com.szte.wmm.greenkiwi.R

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

fun NotificationManager.cancelNotifications() {
    cancelAll()
}