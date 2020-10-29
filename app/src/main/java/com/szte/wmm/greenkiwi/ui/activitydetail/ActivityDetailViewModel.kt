package com.szte.wmm.greenkiwi.ui.activitydetail

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

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
        initLastAddedDate(activity.activityId)
    }

    private fun initLastAddedDate(activityId: Long) {
        uiScope.launch {
            _lastAddedDate.value = getFormattedDate(activityId)
        }
    }

    private suspend fun getFormattedDate(activityId: Long): String {
        return withContext(Dispatchers.IO) {
            val lastAddedTimeStamp = userSelectedActivitiesRepository.getLatestActivity(activityId)?.timeAdded
            val formattedDate = formatDateString(lastAddedTimeStamp)
            formattedDate
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDateString(lastAddedTimeStamp: Long?): String {
        return if (lastAddedTimeStamp != null) {
            SimpleDateFormat("yyyy-MM-dd").format(lastAddedTimeStamp).toString()
        } else {
            "Haven't added yet"
        }
    }

    fun addActivity(activityId: Long) {
        uiScope.launch {
            val currentTime = System.currentTimeMillis()
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
