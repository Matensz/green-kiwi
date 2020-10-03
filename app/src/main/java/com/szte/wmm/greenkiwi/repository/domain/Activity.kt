package com.szte.wmm.greenkiwi.repository.domain

/**
 * Domain model representing an activity.
 */
data class Activity(
    val activityId: Long,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val imageUrl: String,
    val point: Int,
    val category: Category
)