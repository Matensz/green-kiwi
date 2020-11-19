package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivityWithDetails

/**
 * An interface for user selected activities related queries.
 */
interface UserSelectedActivitiesRepository {
    fun getLatestActivity(activityId: Long): UserSelectedActivity?

    suspend fun getLatestXActivities(count: Int): List<UserSelectedActivity>

    suspend fun getLatestXActivitiesWithDetails(count: Int): List<UserSelectedActivityWithDetails>

    suspend fun insertUserSelectedActivity(userSelectedActivity: UserSelectedActivity)

    suspend fun deleteAllAddedActivities()
}
