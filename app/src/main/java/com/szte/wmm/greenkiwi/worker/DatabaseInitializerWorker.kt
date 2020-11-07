package com.szte.wmm.greenkiwi.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.szte.wmm.greenkiwi.data.local.ApplicationDatabase
import com.szte.wmm.greenkiwi.data.local.model.Activity
import com.szte.wmm.greenkiwi.data.local.model.ShopItem
import kotlinx.coroutines.coroutineScope

/**
 * Worker reading initializing database tables with the provided json files.
 */
class DatabaseInitializerWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val ACTIVITIES_JSON_NAME = "activities.json"
        private const val SHOP_ITEMS_JSON_NAME = "shopitems.json"
        private const val TAG = "DatabaseInitializer"
    }

    private lateinit var database: ApplicationDatabase

    override suspend fun doWork(): Result = coroutineScope {
        try {
            database = ApplicationDatabase.getInstance(applicationContext)
            readActivitiesJson()
            readShopItemsJson()
            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    private suspend fun readActivitiesJson() {
        applicationContext.assets.open(ACTIVITIES_JSON_NAME).use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val activityType = object : TypeToken<List<Activity>>() {}.type
                val activityList: List<Activity> = Gson().fromJson(jsonReader, activityType)
                database.activitiesDao().insertAll(activityList)
            }
        }
    }

    private suspend fun readShopItemsJson() {
        applicationContext.assets.open(SHOP_ITEMS_JSON_NAME).use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val shopItemType = object : TypeToken<List<ShopItem>>() {}.type
                val shopItemList: List<ShopItem> = Gson().fromJson(jsonReader, shopItemType)
                database.shopDao().insertAll(shopItemList)
            }
        }
    }
}