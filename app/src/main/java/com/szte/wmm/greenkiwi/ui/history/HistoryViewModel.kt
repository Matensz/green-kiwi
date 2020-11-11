package com.szte.wmm.greenkiwi.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivityWithDetails
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class HistoryViewModel(private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository) : ViewModel() {

    companion object {
        private const val HISTORY_LIST_LENGTH = 15
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

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        initDailyActivityCounter()
        initActivityHistoryList()
        _kiwiImageKey.value = R.string.current_pet_image_key
    }

    private fun initActivityHistoryList() {
        uiScope.launch {
            _activityHistoryList.value = getHistoryList()
        }
    }

    private suspend fun getHistoryList(): List<UserSelectedActivityWithDetails> {
        return withContext(Dispatchers.IO) {
            val historyList = userSelectedActivitiesRepository.getLatestXActivitiesWithDetails(HISTORY_LIST_LENGTH)
            historyList
        }
    }

    private fun initDailyActivityCounter() {
        uiScope.launch {
            _dailyActivityCount.value = getDailyCount()
        }
    }

    private suspend fun getDailyCount(): Int {
        return withContext(Dispatchers.IO) {
            val currentDate = SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())
            val currentDateActivityCount = userSelectedActivitiesRepository.getLatestXActivities(3)
                .map { activity -> activity.timeAdded }
                .map { millis -> SimpleDateFormat("yyyyMMdd").format(millis) }
                .count { dateAdded -> dateAdded == currentDate }
            currentDateActivityCount
        }
    }
}
