package com.szte.wmm.greenkiwi.ui.shop

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.ShopRepository
import com.szte.wmm.greenkiwi.repository.domain.ShopCategory
import com.szte.wmm.greenkiwi.repository.domain.ShopItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * View model for the shop view.
 */
class ShopViewModel(
    private val shopRepository: ShopRepository,
    private val app: Application,
    private val defaultDispatcher: CoroutineDispatcher
) : AndroidViewModel(app) {

    private val sharedPref = app.getSharedPreferences(app.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    private lateinit var shopItems: List<ShopItem>
    private val _filteredItems = MutableLiveData<List<ShopItem>>()
    val filteredItems: LiveData<List<ShopItem>>
        get() = _filteredItems
    private val _categories = MutableLiveData<List<ShopCategory>>()
    val categories: LiveData<List<ShopCategory>>
        get() = _categories
    private var filter = FilterHolder()

    init {
        viewModelScope.launch {
            shopItems = shopRepository.getShopItems()
            _filteredItems.value = shopItems
        }
        _categories.value = ShopCategory.values().toList()
    }

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (this.filter.update(filter, isChecked)) {
            _filteredItems.value = shopItems.filter { it.category.name ==  filter}
        }
    }

    fun buySelectedItem(shopItem: ShopItem) {
        viewModelScope.launch {
            setPurchasedFlagForItem(shopItem.itemId)
            updatePlayerGold(shopItem.price)
        }
    }

    fun useSelectedItem(shopItem: ShopItem) {
        viewModelScope.launch {
            when (shopItem.category) {
                ShopCategory.BACKGROUND -> setCurrentItem(R.string.current_background_key, shopItem.imageResourceName)
                ShopCategory.PET_IMAGE -> setCurrentItem(R.string.current_pet_image_key, shopItem.imageResourceName)
            }
        }
    }

    private suspend fun setPurchasedFlagForItem(itemId: Long) {
        withContext(defaultDispatcher) {
            shopRepository.updateShopItemById(itemId, true)
            shopItems = shopRepository.getShopItems()
        }
        _filteredItems.value = shopItems.filter { filter.currentValue?.equals(it.category.name) ?: true }
    }

    private suspend fun updatePlayerGold(price: Int) {
        return withContext(defaultDispatcher) {
            val playerGoldKey = app.getString(R.string.saved_user_gold_key)
            val currentGold = sharedPref.getLong(playerGoldKey, 0L)
            val updatedGold = currentGold - price.toLong()
            sharedPref.edit().putLong(playerGoldKey, updatedGold).apply()
        }
    }

    private suspend fun setCurrentItem(keyId: Int, resourceName: String) {
        withContext(defaultDispatcher) {
            sharedPref.edit().putString(app.getString(keyId), resourceName).apply()
        }
    }

    /**
     * Class for holding the current shop category filter.
     */
    private class FilterHolder {
        var currentValue: String? = null
            private set

        fun update(changedFilter: String, isChecked: Boolean): Boolean {
            var updateNeeded = false
            if (isChecked) {
                currentValue = changedFilter
                updateNeeded = true
            }
            return updateNeeded
        }
    }
}
