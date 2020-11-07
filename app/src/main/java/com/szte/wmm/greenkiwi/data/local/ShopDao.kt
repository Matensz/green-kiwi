package com.szte.wmm.greenkiwi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.szte.wmm.greenkiwi.data.local.model.ShopItem

/**
 * Room Dao object for the Shop related queries.
 */
@Dao
interface ShopDao {

    @Query("SELECT * FROM shopitems")
    suspend fun getShopItems(): List<ShopItem>

    @Query("UPDATE shopitems SET purchased = :purchased WHERE itemid = :itemId")
    suspend fun updateShopItemById(itemId: Long, purchased: Boolean): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shopItems: List<ShopItem>)

    @Query("UPDATE shopitems SET purchased = 0 WHERE title_resource_name NOT IN (:defaultBackgroundName, :defaultPetImageName)")
    suspend fun resetPurchaseStatuses(defaultBackgroundName: String, defaultPetImageName: String)
}
