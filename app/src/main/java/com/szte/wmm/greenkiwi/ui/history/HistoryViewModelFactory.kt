package com.szte.wmm.greenkiwi.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import kotlinx.coroutines.CoroutineDispatcher

/**
 * ViewModelProvider.Factory implementation for creating HistoryViewModel.
 */
class HistoryViewModelFactory(private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository, private val defaultDispatcher: CoroutineDispatcher)
    : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(userSelectedActivitiesRepository, defaultDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
