package com.szte.wmm.greenkiwi.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * View model for the activities view.
 */
class ActivitiesViewModel(private val activitiesRepository: ActivitiesRepository, private val defaultDispatcher: CoroutineDispatcher) : ViewModel() {

    private lateinit var activities: List<Activity>
    private val _filteredActivities = MutableLiveData<List<Activity>>()
    val filteredActivities: LiveData<List<Activity>>
        get() = _filteredActivities
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>>
        get() = _categories
    private val _navigateToSelectedActivity = MutableLiveData<Activity?>()
    val navigateToSelectedActivity: LiveData<Activity?>
        get() = _navigateToSelectedActivity
    private var filter = FilterHolder()

    init {
        viewModelScope.launch {
            activities = getActivities()
            _categories.value = Category.values().toList()
            _filteredActivities.value = activities
        }
    }

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (this.filter.update(filter, isChecked)) {
            _filteredActivities.value = activities.filter { it.category.name ==  filter}
        }
    }

    private suspend fun getActivities(): List<Activity> {
        return withContext(defaultDispatcher) {
            activitiesRepository.getActivities()
        }
    }

    /**
     * Class for holding the current category filter.
     */
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

    fun displayActivityDetails(activity: Activity) {
        _navigateToSelectedActivity.value = activity
    }

    fun displayActivityDetailsComplete() {
        _navigateToSelectedActivity.value = null
    }
}
