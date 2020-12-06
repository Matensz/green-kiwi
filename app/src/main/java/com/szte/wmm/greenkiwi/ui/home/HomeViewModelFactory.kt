package com.szte.wmm.greenkiwi.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import kotlinx.coroutines.CoroutineDispatcher

/**
 * ViewModelProvider.Factory implementation for creating HomeViewModel.
 */
class HomeViewModelFactory(
    private val activityRepository: ActivitiesRepository,
    private val application: Application,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(activityRepository, application, defaultDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
