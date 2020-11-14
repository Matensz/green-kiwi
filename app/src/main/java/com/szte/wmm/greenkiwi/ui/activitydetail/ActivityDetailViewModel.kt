package com.szte.wmm.greenkiwi.ui.activitydetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * View model for the activity details view.
 */
class ActivityDetailViewModel internal constructor(
    activity: Activity,
    private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository,
    private val app: Application
) : AndroidViewModel(app) {

    private val _selectedActivity = MutableLiveData<Activity>()
    val selectedActivity: LiveData<Activity>
        get() = _selectedActivity
    private val _lastAddedDate = MutableLiveData<Long?>()
    val lastAddedDate: LiveData<Long?>
        get() = _lastAddedDate

    val pointInfo = Transformations.map(selectedActivity) { activity ->
        val infoStr = app.getString(R.string.point_info)
        String.format(infoStr, activity.point)
    }

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        _selectedActivity.value = activity
        initLastAddedDate(activity.activityId)
    }

    private fun initLastAddedDate(activityId: Long) {
        uiScope.launch {
            _lastAddedDate.value = getLastAddedTimeStamp(activityId)
        }
    }

    private suspend fun getLastAddedTimeStamp(activityId: Long): Long? {
        return withContext(Dispatchers.IO) {
            val lastAddedTimeStamp = userSelectedActivitiesRepository.getLatestActivity(activityId)?.timeAdded
            lastAddedTimeStamp
        }
    }

    fun addActivity(activityId: Long, currentTime: Long) {
        uiScope.launch {
            val newActivity = UserSelectedActivity(activityId = activityId, timeAdded = currentTime)
            insertActivityTobDb(newActivity)
            _lastAddedDate.value = getLastAddedTimeStamp(activityId)
        }
    }

    private suspend fun insertActivityTobDb(newActivity: UserSelectedActivity) {
        withContext(Dispatchers.IO) {
            userSelectedActivitiesRepository.insertUserSelectedActivity(newActivity)
        }
    }
}
