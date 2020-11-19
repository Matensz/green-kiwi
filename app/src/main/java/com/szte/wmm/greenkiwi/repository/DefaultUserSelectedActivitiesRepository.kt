package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.data.local.UserSelectedActivitiesDao
import com.szte.wmm.greenkiwi.data.local.model.asDomainModel
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import com.szte.wmm.greenkiwi.repository.domain.asDatabaseModel

/**
 * Default repository implementation for the user selected activity related queries.
 */
class DefaultUserSelectedActivitiesRepository(private val userSelectedActivitiesDao: UserSelectedActivitiesDao) : UserSelectedActivitiesRepository {

    override fun getLatestActivity(activityId: Long) = userSelectedActivitiesDao.getLatestActivity(activityId)?.asDomainModel()

    override suspend fun getLatestXActivities(count: Int) = userSelectedActivitiesDao.getLatestXActivities(count).map { activity -> activity.asDomainModel() }

    override suspend fun getLatestXActivitiesWithDetails(count: Int) = userSelectedActivitiesDao.getLatestXActivitiesWithDetails(count).map { activity -> activity.asDomainModel() }

    override suspend fun insertUserSelectedActivity(userSelectedActivity: UserSelectedActivity) =
        userSelectedActivitiesDao.insertUserSelectedActivity(userSelectedActivity.asDatabaseModel())

    override suspend fun deleteAllAddedActivities() = userSelectedActivitiesDao.deleteAllAddedActivities()

}
