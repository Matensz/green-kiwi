package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.data.local.UserSelectedActivitiesDao
import com.szte.wmm.greenkiwi.data.local.model.asDomainModel
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import com.szte.wmm.greenkiwi.repository.domain.asDatabaseModel

/**
 * Default repository implementation for the Activity related queries.
 */
class UserSelectedActivitiesRepository(private val userSelectedActivitiesDao: UserSelectedActivitiesDao) {

    fun getLatestActivity(activityId: Long) = userSelectedActivitiesDao.getLatestActivity(activityId)?.asDomainModel()

    suspend fun getLatestXActivities(count: Int) = userSelectedActivitiesDao.getLatestXActivities(count).map { activity -> activity.asDomainModel() }

    suspend fun getLatestXActivitiesWithDetails(count: Int) = userSelectedActivitiesDao.getLatestXActivitiesWithDetails(count).map { activity -> activity.asDomainModel() }

    suspend fun insertUserSelectedActivity(userSelectedActivity: UserSelectedActivity) =
        userSelectedActivitiesDao.insertUserSelectedActivity(userSelectedActivity.asDatabaseModel())

    suspend fun deleteAllAddedActivities() = userSelectedActivitiesDao.deleteAllAddedActivities()

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: UserSelectedActivitiesRepository? = null

        fun getInstance(userSelectedActivitiesDao: UserSelectedActivitiesDao) =
            instance ?: synchronized(this) {
                instance ?: UserSelectedActivitiesRepository(userSelectedActivitiesDao).also { instance = it }
            }
    }
}
