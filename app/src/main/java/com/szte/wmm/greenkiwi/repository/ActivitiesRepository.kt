package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.data.local.ActivitiesDao
import com.szte.wmm.greenkiwi.data.local.model.asDomainModel

/**
 * Default repository implementation for the Activity related queries.
 */
class ActivitiesRepository(private val activitiesDao: ActivitiesDao) {

    suspend fun getActivities() = activitiesDao.getActivities().map { it.asDomainModel() }

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: ActivitiesRepository? = null

        fun getInstance(activitiesDao: ActivitiesDao) =
            instance ?: synchronized(this) {
                instance ?: ActivitiesRepository(activitiesDao).also { instance = it }
            }
    }
}
