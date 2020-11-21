package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.repository.domain.Activity

/**
 * Test repository implementation for the activities related queries.
 */
class TestActivitiesRepository : ActivitiesRepository {

    private var activitiesList = mutableListOf<Activity>()

    override suspend fun getActivities(): List<Activity> = activitiesList

    fun addActivities(activities: List<Activity>) = activitiesList.addAll(activities)
}
