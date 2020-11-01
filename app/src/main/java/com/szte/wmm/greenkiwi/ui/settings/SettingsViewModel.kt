package com.szte.wmm.greenkiwi.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import kotlinx.coroutines.*

class SettingsViewModel(private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository, private val app: Application) : AndroidViewModel(app) {

    private val sharedPref = app.getSharedPreferences(app.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun resetUserValues() {
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
