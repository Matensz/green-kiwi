package com.szte.wmm.greenkiwi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.szte.wmm.greenkiwi.data.local.model.Activity
import com.szte.wmm.greenkiwi.data.local.model.ShopItem
import com.szte.wmm.greenkiwi.data.local.model.UserSelectedActivity
import com.szte.wmm.greenkiwi.worker.DatabaseInitializerWorker

/**
 * Room database object for the application.
 */
@Database(entities = [Activity::class, UserSelectedActivity::class, ShopItem::class], version = 2, exportSchema = false)
abstract class ApplicationDatabase : RoomDatabase() {

    abstract fun activitiesDao(): ActivitiesDao
    abstract fun userSelectedActivitiesDao(): UserSelectedActivitiesDao
    abstract fun shopDao(): ShopDao

    companion object {
        private const val DATABASE_NAME = "green-kiwi-db"

        // For Singleton instantiation
        @Volatile private var instance: ApplicationDatabase? = null

        fun getInstance(context: Context): ApplicationDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database
        private fun buildDatabase(context: Context): ApplicationDatabase {
            return Room.databaseBuilder(context, ApplicationDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val request = OneTimeWorkRequestBuilder<DatabaseInitializerWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    }
                })
                .build()
        }
    }
}