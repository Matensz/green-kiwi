package com.szte.wmm.greenkiwi.repository

import com.szte.wmm.greenkiwi.repository.domain.Activity

/**
 * An interface for the activities related queries.
 */
interface ActivitiesRepository {

    suspend fun getActivities(): List<Activity>
}
