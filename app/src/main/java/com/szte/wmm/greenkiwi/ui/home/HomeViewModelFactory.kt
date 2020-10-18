package com.szte.wmm.greenkiwi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ViewModelProvider.Factory implementation for creating HomeViewModel.
 */
class HomeViewModelFactory(private val currentPoints: Long, private val expBaseNumber: Int) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(currentPoints, expBaseNumber) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}