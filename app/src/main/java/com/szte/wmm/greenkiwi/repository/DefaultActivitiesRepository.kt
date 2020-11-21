package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.data.local.ActivitiesDao
import com.szte.wmm.greenkiwi.data.local.model.asDomainModel

/**
 * Default repository implementation for the activities related queries.
 */
class DefaultActivitiesRepository(private val activitiesDao: ActivitiesDao) : ActivitiesRepository {

    override suspend fun getActivities() = activitiesDao.getActivities().map { it.asDomainModel() }
}
