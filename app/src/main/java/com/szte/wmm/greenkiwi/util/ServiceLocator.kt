package com.szte.wmm.greenkiwi.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.szte.wmm.greenkiwi.data.local.ApplicationDatabase
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import com.szte.wmm.greenkiwi.repository.DefaultActivitiesRepository
import com.szte.wmm.greenkiwi.repository.DefaultShopRepository
import com.szte.wmm.greenkiwi.repository.DefaultUserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.ShopRepository
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.worker.DatabaseInitializerWorker

/**
 * A service locator object to manage dependencies in the application.
 */
object ServiceLocator {

    private const val DATABASE_NAME = "green-kiwi-db"

    @Volatile
    private var database: ApplicationDatabase? = null

    @Volatile
    var activitiesRepository: ActivitiesRepository? = null
        @VisibleForTesting set

    @Volatile
    var userSelectedActivitiesRepository: UserSelectedActivitiesRepository? = null
        @VisibleForTesting set

    @Volatile
    var shopRepository: ShopRepository? = null
        @VisibleForTesting set

    fun getActivitiesRepository(context: Context): ActivitiesRepository {
        synchronized(this) {
            return activitiesRepository ?: createActivitiesRepository(context)
        }
    }

    private fun createActivitiesRepository(context: Context): ActivitiesRepository {
        val repo = DefaultActivitiesRepository(getDataBase(context).activitiesDao())
        activitiesRepository = repo
        return repo
    }

    fun getUserSelectedActivitiesRepository(context: Context): UserSelectedActivitiesRepository {
        synchronized(this) {
            return userSelectedActivitiesRepository ?: createUserSelectedActivitiesRepository(context)
        }
    }

    private fun createUserSelectedActivitiesRepository(context: Context): UserSelectedActivitiesRepository {
        val repo = DefaultUserSelectedActivitiesRepository(getDataBase(context).userSelectedActivitiesDao())
        userSelectedActivitiesRepository = repo
        return repo
    }

    fun getShopRepository(context: Context): ShopRepository {
        synchronized(this) {
            return shopRepository ?: createShopRepository(context)
        }
    }

    private fun createShopRepository(context: Context): ShopRepository {
        val repo = DefaultShopRepository(getDataBase(context).shopDao())
        shopRepository = repo
        return repo
    }

    private fun getDataBase(context: Context): ApplicationDatabase {
        synchronized(this) {
            return database ?: createDataBase(context)
        }
    }

    private fun createDataBase(context: Context): ApplicationDatabase {
        val db = Room.databaseBuilder(context, ApplicationDatabase::class.java, DATABASE_NAME)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val request = OneTimeWorkRequestBuilder<DatabaseInitializerWorker>().build()
                    WorkManager.getInstance(context).enqueue(request)
                }
            })
            .build()
        database = db
        return db
    }
}
