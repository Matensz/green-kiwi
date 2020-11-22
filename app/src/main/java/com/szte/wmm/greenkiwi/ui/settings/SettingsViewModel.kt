package com.szte.wmm.greenkiwi.ui.settings

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.szte.wmm.greenkiwi.HungerAlarmReceiver
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.ShopRepository
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * View model for the settings view.
 */
class SettingsViewModel(
    private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository,
    private val shopRepository: ShopRepository,
    private val app: Application,
    private val defaultDispatcher: CoroutineDispatcher
) : AndroidViewModel(app) {

    companion object {
        private const val HUNGER_NOTIFICATION_ID = 0
        private const val DEFAULT_BACKGROUND_RESOURCE_NAME = "default_wallpaper_name"
        private const val DEFAULT_PET_IMAGE_RESOURCE_NAME = "green_kiwi_name"
    }

    private val sharedPref = app.getSharedPreferences(app.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(app, HungerAlarmReceiver::class.java)
    private val notifyPendingIntent: PendingIntent

    init {
        notifyPendingIntent = PendingIntent.getBroadcast(
            app,
            HUNGER_NOTIFICATION_ID,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun saveNightModeSettings(isChecked: Boolean) {
        viewModelScope.launch {
            updateNightModePreferences(isChecked)
        }
    }

    private suspend fun updateNightModePreferences(isChecked: Boolean) {
        withContext(defaultDispatcher) {
            val nightModeSettingKey = app.getString(R.string.night_mode_setting_key)
            sharedPref.edit().putBoolean(nightModeSettingKey, isChecked).apply()
        }
    }

    fun resetUserValues() {
        alarmManager.cancel(notifyPendingIntent)
        viewModelScope.launch {
            cleanUpSharedPreferences()
            cleanUpDatabase()
        }
    }

    private suspend fun cleanUpSharedPreferences() {
        withContext(defaultDispatcher) {
            with(sharedPref.edit()) {
                remove(app.getString(R.string.saved_user_points_key))
                remove(app.getString(R.string.hunger_timer_key))
                remove(app.getString(R.string.saved_user_gold_key))
                remove(app.getString(R.string.last_saved_activity_date_key))
                remove(app.getString(R.string.daily_activity_counter_key))
                remove(app.getString(R.string.pet_nickname_key))
                remove(app.getString(R.string.current_background_key))
                remove(app.getString(R.string.current_pet_image_key))
                apply()
            }
        }
        Timber.d("User preferences deleted from shared preferences")
    }

    private suspend fun cleanUpDatabase() {
        withContext(defaultDispatcher) {
            userSelectedActivitiesRepository.deleteAllAddedActivities()
            shopRepository.resetPurchaseStatuses(DEFAULT_BACKGROUND_RESOURCE_NAME, DEFAULT_PET_IMAGE_RESOURCE_NAME)
        }
        Timber.d("User selected activities deleted from database")
    }
}
