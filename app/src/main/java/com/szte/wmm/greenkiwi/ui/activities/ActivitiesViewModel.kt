package com.szte.wmm.greenkiwi.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.Category
import kotlinx.coroutines.launch

class ActivitiesViewModel internal constructor(
    activitiesRepository: ActivitiesRepository
) : ViewModel() {

    private val _activities = MutableLiveData<List<Activity>>()
    val activities: LiveData<List<Activity>>
        get() = _activities
    private val _filteredActivities = MutableLiveData<List<Activity>>()
    val filteredActivities: LiveData<List<Activity>>
        get() = _filteredActivities
    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>>
        get() = _categories
    private var filter = FilterHolder()

    init {
        viewModelScope.launch {
            _activities.value = activitiesRepository.getActivities()
            _categories.value = Category.values().map { it.name }
            _filteredActivities.value = _activities.value.orEmpty()
        }
    }

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (this.filter.update(filter, isChecked)) {
            _filteredActivities.value = _activities.value?.filter { it.category.name ==  filter}.orEmpty()
        }
    }

    private class FilterHolder {
        var currentValue: String? = null
            private set

        fun update(changedFilter: String, isChecked: Boolean): Boolean {
            var updateNeeded = false
            if (isChecked) {
                currentValue = changedFilter
                updateNeeded = true
            }
            return updateNeeded
        }
    }
}