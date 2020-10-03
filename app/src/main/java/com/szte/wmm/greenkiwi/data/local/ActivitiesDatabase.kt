package com.szte.wmm.greenkiwi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.szte.wmm.greenkiwi.data.local.model.Activity
import com.szte.wmm.greenkiwi.worker.ActivityInitializerWorker

/**
 * Room database object for the application.
 */
@Database(entities = [Activity::class], version = 1, exportSchema = false)
abstract class ActivitiesDatabase : RoomDatabase() {

    abstract fun activitiesDao(): ActivitiesDao

    companion object {
        private const val DATABASE_NAME = "green-kiwi-db"

        // For Singleton instantiation
        @Volatile private var instance: ActivitiesDatabase? = null

        fun getInstance(context: Context): ActivitiesDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database
        private fun buildDatabase(context: Context): ActivitiesDatabase {
            return Room.databaseBuilder(context, ActivitiesDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val request = OneTimeWorkRequestBuilder<ActivityInitializerWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    }
                })
                .build()
        }
    }
}