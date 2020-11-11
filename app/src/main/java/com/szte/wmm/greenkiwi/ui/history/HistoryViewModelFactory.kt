package com.szte.wmm.greenkiwi.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository

/**
 * ViewModelProvider.Factory implementation for creating HistoryViewModel.
 */
class HistoryViewModelFactory(private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(userSelectedActivitiesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
