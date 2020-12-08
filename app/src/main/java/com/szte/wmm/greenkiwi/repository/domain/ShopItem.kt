package com.szte.wmm.greenkiwi.repository.domain

/**
 * Domain model representing a shop item.
 */
data class ShopItem(
    val itemId: Long,
    val titleResourceName: String,
    val imageResourceName: String,
    val price: Int,
    val category: ShopCategory,
    val purchased: Boolean,
    val lastPurchased: Boolean
)
