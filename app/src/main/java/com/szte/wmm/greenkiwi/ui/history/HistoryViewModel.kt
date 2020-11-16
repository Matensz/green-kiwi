package com.szte.wmm.greenkiwi.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivityWithDetails
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

/**
 * View model for the history view.
 */
class HistoryViewModel(private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository, private val defaultDispatcher: CoroutineDispatcher) : ViewModel() {

    companion object {
        private const val HISTORY_LIST_LENGTH = 15
        private const val DAILY_COUNTER_MAX_VALUE = 3
    }

    private val _dailyActivityCount = MutableLiveData<Int>()
    val dailyActivityCount: LiveData<Int>
        get() = _dailyActivityCount
    private val _activityHistoryList = MutableLiveData<List<UserSelectedActivityWithDetails>>()
    val activityHistoryList: LiveData<List<UserSelectedActivityWithDetails>>
        get() = _activityHistoryList
    private val _kiwiImageKey = MutableLiveData<Int>()
    val kiwiImageKey: LiveData<Int>
        get() = _kiwiImageKey

    init {
        initDailyActivityCounter()
        initActivityHistoryList()
        _kiwiImageKey.value = R.string.current_pet_image_key
    }

    private fun initActivityHistoryList() {
        viewModelScope.launch {
            _activityHistoryList.value = getHistoryList()
        }
    }

    private suspend fun getHistoryList(): List<UserSelectedActivityWithDetails> {
        return withContext(defaultDispatcher) {
            val historyList = userSelectedActivitiesRepository.getLatestXActivitiesWithDetails(HISTORY_LIST_LENGTH)
            historyList
        }
    }

    private fun initDailyActivityCounter() {
        viewModelScope.launch {
            _dailyActivityCount.value = getDailyCount()
        }
    }

    private suspend fun getDailyCount(): Int {
        return withContext(defaultDispatcher) {
            val currentDate = SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())
            val currentDateActivityCount = userSelectedActivitiesRepository.getLatestXActivities(DAILY_COUNTER_MAX_VALUE)
                .map { activity -> activity.timeAdded }
                .map { millis -> SimpleDateFormat("yyyyMMdd").format(millis) }
                .count { dateAdded -> dateAdded == currentDate }
            currentDateActivityCount
        }
    }
}
