package com.szte.wmm.greenkiwi.ui.activitydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * View model for the activity details view.
 */
class ActivityDetailViewModel(
    activity: Activity,
    private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _selectedActivity = MutableLiveData<Activity>()
    val selectedActivity: LiveData<Activity>
        get() = _selectedActivity
    private val _lastAddedDate = MutableLiveData<Long?>()
    val lastAddedDate: LiveData<Long?>
        get() = _lastAddedDate

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
    }
}
