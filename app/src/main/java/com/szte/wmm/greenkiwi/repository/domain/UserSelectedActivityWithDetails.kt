package com.szte.wmm.greenkiwi.repository.domain

/**
 * Domain model representing a user selected activity with activity details.
 */
data class UserSelectedActivityWithDetails(
    val activityId: Long,
    val title: String,
    val point: Int,
    val gold: Int,
    val category: Category,
    val timeAdded: String
)
