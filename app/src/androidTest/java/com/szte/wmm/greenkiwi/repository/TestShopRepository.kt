package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.repository.domain.ShopItem

/**
 * Test repository implementation for the shop related queries.
 */
class TestShopRepository : ShopRepository {

    private var shopItemsList = mutableListOf<ShopItem>()

    override suspend fun getShopItems() = shopItemsList

    override suspend fun updateShopItemById(itemId: Long, purchased: Boolean): Int {
        val index = shopItemsList.indexOfFirst { it.itemId == itemId }
        var result = 0
        if (index != -1) {
            val currentItem = shopItemsList[index]
            shopItemsList[index] = createShopItem(currentItem, true)
            result = 1
        }
        return result
    }

    override suspend fun resetPurchaseStatuses(defaultBackgroundName: String, defaultPetImageName: String) {
        shopItemsList = shopItemsList.filter { it.titleResourceName != defaultBackgroundName && it.titleResourceName != defaultPetImageName }
            .map { createShopItem(it, false) }
            .toMutableList()
    }

    fun addShopItems(shopItems: List<ShopItem>) {
        shopItemsList.addAll(shopItems)
    }

    private fun createShopItem(current: ShopItem, purchased: Boolean): ShopItem {
        return ShopItem(current.itemId, current.titleResourceName, current.imageResourceName, current.price, current.category, purchased, false)
    }
}
