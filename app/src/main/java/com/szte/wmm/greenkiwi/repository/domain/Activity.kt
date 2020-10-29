package com.szte.wmm.greenkiwi.repository.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Domain model representing an activity.
 */
@Parcelize
data class Activity(
    val activityId: Long,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val imageUrl: String,
    val point: Int,
    val gold: Int,
    val category: Category
) : Parcelable