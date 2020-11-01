package com.szte.wmm.greenkiwi.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.ui.home.context.HomeDataContext

/**
 * ViewModelProvider.Factory implementation for creating HomeViewModel.
 */
class HomeViewModelFactory(private val context: HomeDataContext, private val application: Application) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(context, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}