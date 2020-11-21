package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.data.local.ShopDao
import com.szte.wmm.greenkiwi.data.local.model.asDomainModel

/**
 * Default repository implementation for the shop related queries.
 */
class DefaultShopRepository(private val shopDao: ShopDao) : ShopRepository {

    override suspend fun getShopItems() = shopDao.getShopItems().map { item -> item.asDomainModel() }

    override suspend fun updateShopItemById(itemId: Long, purchased: Boolean) = shopDao.updateShopItemById(itemId, purchased)

    override suspend fun resetPurchaseStatuses(defaultBackgroundName: String, defaultPetImageName: String) =
        shopDao.resetPurchaseStatuses(defaultBackgroundName, defaultPetImageName)

}
