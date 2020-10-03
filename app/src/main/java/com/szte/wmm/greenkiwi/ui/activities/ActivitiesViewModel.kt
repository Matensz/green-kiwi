package com.szte.wmm.greenkiwi.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import kotlinx.coroutines.launch

class ActivitiesViewModel internal constructor(
    activitiesRepository: ActivitiesRepository
) : ViewModel() {

    private val _activities = MutableLiveData<List<Activity>>()
    val activities: LiveData<List<Activity>>
        get() = _activities

    init {
        viewModelScope.launch {
            _activities.value = activitiesRepository.getActivities()
        }
    }
}