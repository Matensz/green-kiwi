package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.repository.domain.ShopItem

/**
 * An interface for the shop related queries.
 */
interface ShopRepository {
    suspend fun getShopItems(): List<ShopItem>

    suspend fun updateShopItemById(itemId: Long, purchased: Boolean): Int

    suspend fun resetPurchaseStatuses(defaultBackgroundName: String, defaultPetImageName: String)
}
