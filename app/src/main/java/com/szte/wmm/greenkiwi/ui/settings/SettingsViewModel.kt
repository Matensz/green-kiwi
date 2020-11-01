package com.szte.wmm.greenkiwi.ui.settings

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.szte.wmm.greenkiwi.HungerAlarmReceiver
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import kotlinx.coroutines.*

class SettingsViewModel(private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository, private val app: Application) : AndroidViewModel(app) {

    companion object {
        private const val HUNGER_NOTIFICATION_ID = 0
    }

    private val sharedPref = app.getSharedPreferences(app.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(app, HungerAlarmReceiver::class.java)
    private val notifyPendingIntent: PendingIntent

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            HUNGER_NOTIFICATION_ID,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun resetUserValues() {
        alarmManager.cancel(notifyPendingIntent)
        uiScope.launch {
            cleanUpSharedPreferences()
            cleanUpDatabase()
        }
    }

    private suspend fun cleanUpSharedPreferences() {
        withContext(Dispatchers.IO) {
            with(sharedPref.edit()) {
                remove(app.getString(R.string.saved_user_points_key))
                remove(app.getString(R.string.hunger_timer_key))
                remove(app.getString(R.string.saved_user_gold_key))
                remove(app.getString(R.string.last_saved_activity_date_key))
                remove(app.getString(R.string.daily_activity_counter_key))
                remove(app.getString(R.string.pet_nickname_key))
                apply()
            }
        }
    }

    private suspend fun cleanUpDatabase() {
        withContext(Dispatchers.IO) {
            userSelectedActivitiesRepository.deleteAllAddedActivities()
        }
    }
}
