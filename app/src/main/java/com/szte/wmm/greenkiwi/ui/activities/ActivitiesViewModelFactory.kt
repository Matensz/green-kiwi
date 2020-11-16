package com.szte.wmm.greenkiwi.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import kotlinx.coroutines.CoroutineDispatcher

/**
 * ViewModelProvider.Factory implementation for creating ActivitiesViewModel.
 */
class ActivitiesViewModelFactory(private val activitiesRepository: ActivitiesRepository, private val defaultDispatcher: CoroutineDispatcher) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivitiesViewModel::class.java)) {
            return ActivitiesViewModel(activitiesRepository, defaultDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
