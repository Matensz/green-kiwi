package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.repository.domain.Category
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivityWithDetails

/**
 * Test repository implementation for the user selected activity related queries.
 */
class TestUserSelectedActivitiesRepository : UserSelectedActivitiesRepository {

    companion object {
        private const val TEST_CURRENT_TIME_MILLIS = 4102354800000
        private const val TEST_CURRENT_TIME = "2099.12.31"
    }

    private var activitiesList = mutableListOf<UserSelectedActivity>()

    override fun getLatestActivity(activityId: Long) = if (activitiesList.isNotEmpty()) activitiesList.last() else null

    override suspend fun getLatestXActivities(count: Int): List<UserSelectedActivity> =
        if (activitiesList.isNotEmpty()) activitiesList.reversed().take(count) else listOf()

    override suspend fun getLatestXActivitiesWithDetails(count: Int): List<UserSelectedActivityWithDetails> =
        if (activitiesList.isNotEmpty()) activitiesList.reversed().take(count).map { createActivityWithDetails(it.activityId) } else listOf()

    override suspend fun insertUserSelectedActivity(userSelectedActivity: UserSelectedActivity) {
        activitiesList.add(UserSelectedActivity(activityId = userSelectedActivity.activityId, timeAdded = TEST_CURRENT_TIME_MILLIS))
    }

    override suspend fun deleteAllAddedActivities() = activitiesList.clear()

    fun addUserSelectedActivities(activities: List<UserSelectedActivity>) {
        activitiesList.addAll(activities.reversed())
    }

    private fun createActivityWithDetails(id: Long) =
        UserSelectedActivityWithDetails(id, "title", 1, 1, Category.OTHER, TEST_CURRENT_TIME)
}
