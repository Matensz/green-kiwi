package com.szte.wmm.greenkiwi.ui.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.repository.ShopRepository
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import kotlinx.coroutines.CoroutineDispatcher

/**
 * ViewModelProvider.Factory implementation for creating SettingsViewModel.
 */
class SettingsViewModelFactory(
    private val userSelectedActivitiesRepository: UserSelectedActivitiesRepository,
    private val shopRepository: ShopRepository,
    private val application: Application,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(userSelectedActivitiesRepository, shopRepository, application, defaultDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
