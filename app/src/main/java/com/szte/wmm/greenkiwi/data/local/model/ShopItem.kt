package com.szte.wmm.greenkiwi.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.szte.wmm.greenkiwi.repository.domain.ShopCategory

/**
 * Database entity representing a shop item.
 */
@Entity(tableName = "shopitems")
data class ShopItem(
    @PrimaryKey @ColumnInfo(name = "itemid") val itemId: Long,
    @ColumnInfo(name = "title_resource_name") val titleResourceName: String,
    @ColumnInfo(name = "image_resource_name") val imageResourceName: String,
    val price: Int,
    val category: Int,
    val purchased: Boolean
)

/**
 * Extension function converting an entity shop item to a domain shop item.
 */
fun ShopItem.asDomainModel(): com.szte.wmm.greenkiwi.repository.domain.ShopItem {
    return com.szte.wmm.greenkiwi.repository.domain.ShopItem(
        itemId,
        titleResourceName,
        imageResourceName,
        price,
        ShopCategory.values().firstOrNull { it.id == category } ?: ShopCategory.PET_IMAGE,
        purchased,
    )
}