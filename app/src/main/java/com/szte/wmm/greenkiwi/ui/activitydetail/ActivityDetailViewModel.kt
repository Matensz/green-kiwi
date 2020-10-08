package com.szte.wmm.greenkiwi.ui.activitydetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.domain.Activity

class ActivityDetailViewModel(activity: Activity, app: Application) : AndroidViewModel(app) {

    private val _selectedActivity = MutableLiveData<Activity>()
    val selectedActivity: LiveData<Activity>
        get() = _selectedActivity

    val pointInfo = Transformations.map(selectedActivity) { activity ->
        val infoStr = app.getString(R.string.point_info)
        String.format(infoStr, activity.point)
    }

    init {
        _selectedActivity.value = activity
    }

    fun addActivity(activityId: Long) {
        //TODO implement logic
    }
}
