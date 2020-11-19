package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.data.local.ShopDao
import com.szte.wmm.greenkiwi.data.local.model.asDomainModel

/**
 * Default repository implementation for the shop related queries.
 */
class ShopRepository(private val shopDao: ShopDao) {

    suspend fun getShopItems() = shopDao.getShopItems().map { item -> item.asDomainModel() }

    suspend fun updateShopItemById(itemId: Long, purchased: Boolean) = shopDao.updateShopItemById(itemId, purchased)

    suspend fun resetPurchaseStatuses(defaultBackgroundName: String, defaultPetImageName: String) = shopDao.resetPurchaseStatuses(defaultBackgroundName, defaultPetImageName)

}
