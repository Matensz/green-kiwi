package com.szte.wmm.greenkiwi.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository

/**
 * ViewModelProvider.Factory implementation for creating NotificationsViewModel.
 */
class NotificationsViewModelFactory(private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            return NotificationsViewModel(userSelectedActivitiesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
