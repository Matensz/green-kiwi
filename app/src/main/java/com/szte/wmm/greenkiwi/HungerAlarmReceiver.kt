package com.szte.wmm.greenkiwi

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.szte.wmm.greenkiwi.util.sendNotification

class HungerAlarmReceiver: BroadcastReceiver() {

    companion object {
        private const val HUNGER_NOTIFICATION_ID = 0
    }

    override fun onReceive(context: Context, intent: Intent) {

        val channelId = context.getString(R.string.pet_hunger_channel_id)
        val text = context.getString(R.string.pet_hunger_text)
        val bigText = context.getString(R.string.pet_hunger_bigtext)

        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(HUNGER_NOTIFICATION_ID, channelId, text, bigText, context)
    }
}