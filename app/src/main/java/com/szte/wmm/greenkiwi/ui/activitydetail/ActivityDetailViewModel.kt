package com.szte.wmm.greenkiwi.ui.activitydetail

import android.app.Application
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import com.szte.wmm.greenkiwi.util.isDayBeforeDate
import com.szte.wmm.greenkiwi.util.isSameDay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * View model for the activity details view.
 */
class ActivityDetailViewModel(
    activity: Activity,
    private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository,
    private val app: Application,
    private val defaultDispatcher: CoroutineDispatcher
) : AndroidViewModel(app) {

    private val _selectedActivity = MutableLiveData<Activity>()
    val selectedActivity: LiveData<Activity>
        get() = _selectedActivity
    private val _lastAddedDate = MutableLiveData<Long?>()
    val lastAddedDate: LiveData<Long?>
        get() = _lastAddedDate

    private val sharedPreferences = app.getSharedPreferences(app.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    private val dailyActivityCounterKey = app.getString(R.string.daily_activity_counter_key)
    private val lastSavedDateKey = app.getString(R.string.last_saved_activity_date_key)

    init {
        _selectedActivity.value = activity
        initLastAddedDate(activity.activityId)
    }

    private fun initLastAddedDate(activityId: Long) {
        viewModelScope.launch {
            _lastAddedDate.value = getLastAddedTimeStamp(activityId)
        }
    }

    private suspend fun getLastAddedTimeStamp(activityId: Long): Long? {
        return withContext(defaultDispatcher) {
            val lastAddedTimeStamp = userSelectedActivitiesRepository.getLatestActivity(activityId)?.timeAdded
            lastAddedTimeStamp
        }
    }

    fun addActivity(activityId: Long, currentTime: Long) {
        viewModelScope.launch {
            val newActivity = UserSelectedActivity(activityId = activityId, timeAdded = currentTime)
            insertActivityTobDb(newActivity)
            _lastAddedDate.value = getLastAddedTimeStamp(activityId)
        }
    }

    private suspend fun insertActivityTobDb(newActivity: UserSelectedActivity) {
        withContext(defaultDispatcher) {
            userSelectedActivitiesRepository.insertUserSelectedActivity(newActivity)
        }
        Timber.d("New user selected activity with activityId ${newActivity.activityId} inserted to database")
    }

    fun updatePlayerValue(valueToAdd: Int, defaultValueResId: Int, keyResId: Int) {
        val defaultValue = app.resources.getInteger(defaultValueResId).toLong()
        val sharedPrefKeyForValue = app.getString(keyResId)
        val currentValue = sharedPreferences.getLong(sharedPrefKeyForValue, defaultValue)
        val updatedValue = currentValue + valueToAdd.toLong()
        viewModelScope.launch {
            updateValueInPrefs(sharedPrefKeyForValue, updatedValue)
        }
    }

    private suspend fun updateValueInPrefs(keyForValue: String, updatedValue: Long) {
        withContext(defaultDispatcher) {
            sharedPreferences.edit().putLong(keyForValue, updatedValue).apply()
        }
    }

    fun getUpdatedDailyCounter(currentTime: Long): Int {
        val defaultLastDate = currentTime - SystemClock.elapsedRealtime()
        val lastSavedDate = sharedPreferences.getLong(lastSavedDateKey, defaultLastDate)
        var dailyCount = 0
        if (lastSavedDate.isDayBeforeDate(currentTime)) {
            dailyCount++
            viewModelScope.launch {
                updateLastSavedDateAndCounter(currentTime, dailyCount)
            }
        } else if (lastSavedDate.isSameDay(currentTime)) {
            dailyCount = sharedPreferences.getInt(dailyActivityCounterKey, 0) + 1
            viewModelScope.launch {
                updateCounter(dailyCount)
            }
        }
        return dailyCount
    }

    private suspend fun updateLastSavedDateAndCounter(currentTime: Long, dailyCount: Int) {
        withContext(defaultDispatcher) {
            with(sharedPreferences.edit()) {
                putLong(lastSavedDateKey, currentTime)
                putInt(dailyActivityCounterKey, dailyCount)
                apply()
            }
        }
    }

    private suspend fun updateCounter(dailyCount: Int) {
        withContext(defaultDispatcher) {
            sharedPreferences.edit().putInt(dailyActivityCounterKey, dailyCount).apply()
        }
    }
}
