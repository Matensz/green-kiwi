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
import com.szte.wmm.greenkiwi.util.formatDateString
import kotlinx.coroutines.*

class ActivityDetailViewModel internal constructor(
    activity: Activity,
    private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository,
    private val app: Application
) : AndroidViewModel(app) {

    private val _selectedActivity = MutableLiveData<Activity>()
    val selectedActivity: LiveData<Activity>
        get() = _selectedActivity
    private val _lastAddedDate = MutableLiveData<String>()
    val lastAddedDate: LiveData<String>
        get() = _lastAddedDate

    val pointInfo = Transformations.map(selectedActivity) { activity ->
        val infoStr = app.getString(R.string.point_info)
        String.format(infoStr, activity.point)
    }

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        _selectedActivity.value = activity
        updateLastAddedDate(activity.activityId)
    }

    private fun updateLastAddedDate(activityId: Long) {
        uiScope.launch {
            _lastAddedDate.value = getFormattedDate(activityId)
        }
    }

    private suspend fun getFormattedDate(activityId: Long): String {
        return withContext(Dispatchers.IO) {
            val lastAddedTimeStamp = userSelectedActivitiesRepository.getLatestActivity(activityId)?.timeAdded
            formatDateString(lastAddedTimeStamp, app.getString(R.string.last_added_date_default))
        }
    }

    fun addActivity(activityId: Long, currentTime: Long) {
        uiScope.launch {
            val newActivity = UserSelectedActivity(activityId = activityId, timeAdded = currentTime)
            insertActivityTobDb(newActivity)
        }
    }

    private suspend fun insertActivityTobDb(newActivity: UserSelectedActivity) {
        withContext(Dispatchers.IO) {
            userSelectedActivitiesRepository.insertUserSelectedActivity(newActivity)
        }
    }
}
