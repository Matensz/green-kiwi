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
import kotlinx.coroutines.coroutineScope

/**
 * Worker reading the default activities from a json file and populating it to the database.
 */
class ActivityInitializerWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(ACTIVITIES_JSON_NAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val activityType = object : TypeToken<List<Activity>>() {}.type
                    val activityList: List<Activity> = Gson().fromJson(jsonReader, activityType)

                    val database = ApplicationDatabase.getInstance(applicationContext)
                    database.activitiesDao().insertAll(activityList)

                    Result.success()
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val ACTIVITIES_JSON_NAME = "activities.json"
        private const val TAG = "ActivityInitializer"
    }
}