package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.data.local.ShopDao
import com.szte.wmm.greenkiwi.data.local.model.asDomainModel

/**
 * Default repository implementation for the Shop related queries.
 */
class ShopRepository(private val shopDao: ShopDao) {

    suspend fun getShopItems() = shopDao.getShopItems().map { item -> item.asDomainModel() }

    suspend fun updateShopItemById(itemId: Long, purchased: Boolean) = shopDao.updateShopItemById(itemId, purchased)

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: ShopRepository? = null

        fun getInstance(shopDao: ShopDao) =
            instance ?: synchronized(this) {
                instance ?: ShopRepository(shopDao).also { instance = it }
            }
    }
}