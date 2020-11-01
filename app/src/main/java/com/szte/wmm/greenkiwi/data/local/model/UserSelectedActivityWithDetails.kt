package com.szte.wmm.greenkiwi.data.local.model

import com.szte.wmm.greenkiwi.repository.domain.Category
import com.szte.wmm.greenkiwi.util.formatDateString

/**
 * Response entity for the joined query for user selected activities with activity details.
 */
data class UserSelectedActivityWithDetails(
    val activityId: Long,
    val title: String,
    val point: Int,
    val gold: Int,
    val categoryId: Long,
    val timeAdded: Long
)

/**
 * Extension function converting an entity activity to a domain activity.
 */
fun UserSelectedActivityWithDetails.asDomainModel(): com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivityWithDetails {
    return com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivityWithDetails(
        activityId,
        title,
        point,
        gold,
        Category.values().firstOrNull { it.id == categoryId } ?: Category.OTHER,
        formatDateString(timeAdded)
    )
}
