package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.data.local.ActivitiesDao
import com.szte.wmm.greenkiwi.data.local.model.asDomainModel

/**
 * Default repository implementation for the activities related queries.
 */
class ActivitiesRepository(private val activitiesDao: ActivitiesDao) {

    suspend fun getActivities() = activitiesDao.getActivities().map { it.asDomainModel() }

}
