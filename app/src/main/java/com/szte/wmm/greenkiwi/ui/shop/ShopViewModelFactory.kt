package com.szte.wmm.greenkiwi.ui.shop

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.repository.ShopRepository
import kotlinx.coroutines.CoroutineDispatcher

/**
 * ViewModelProvider.Factory implementation for creating ShopViewModel.
 */
class ShopViewModelFactory(
    private val shopRepository: ShopRepository,
    private val app: Application,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            return ShopViewModel(shopRepository, app, defaultDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
