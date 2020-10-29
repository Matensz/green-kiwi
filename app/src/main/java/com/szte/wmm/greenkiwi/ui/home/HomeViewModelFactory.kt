package com.szte.wmm.greenkiwi.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository

/**
 * ViewModelProvider.Factory implementation for creating HomeViewModel.
 */
class HomeViewModelFactory(
    private val currentPoints: Long,
    private val expBaseNumber: Int,
    private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(currentPoints, expBaseNumber, userSelectedActivitiesRepository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}