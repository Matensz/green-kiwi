package com.szte.wmm.greenkiwi.repository.domain

/**
 * Domain model representing an activity selected by the user.
 */
data class UserSelectedActivity(
    var id: Long = 0L,
    var activityId: Long,
    var timeAdded: Long
)

/**
 * Extension function converting a domain activity to an entity activity.
 */
fun UserSelectedActivity.asDatabaseModel(): com.szte.wmm.greenkiwi.data.local.model.UserSelectedActivity {
    return com.szte.wmm.greenkiwi.data.local.model.UserSelectedActivity(
        id,
        activityId,
        timeAdded
    )
}