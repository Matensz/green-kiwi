package com.szte.wmm.greenkiwi.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.szte.wmm.greenkiwi.MainActivity
import com.szte.wmm.greenkiwi.R

fun NotificationManager.sendNotification(notificationId: Int, notificationChannelId: String, message: String, applicationContext: Context) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationId,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val petImage = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.kiwi)

    val notificationBuilder = NotificationCompat.Builder(applicationContext, notificationChannelId)
        .setSmallIcon(R.drawable.kiwi)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(message)
        .setLargeIcon(petImage)
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(notificationId, notificationBuilder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}