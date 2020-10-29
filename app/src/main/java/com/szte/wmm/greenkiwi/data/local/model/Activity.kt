package com.szte.wmm.greenkiwi.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.szte.wmm.greenkiwi.repository.domain.Category

/**
 * Database entity representing an activity.
 */
@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey @ColumnInfo(name = "activityid") var activityId: Long,
    var title: String,
    var description: String,
    @ColumnInfo(name = "thumbnail_url") var thumbnailUrl: String,
    @ColumnInfo(name = "image_url") var imageUrl: String,
    var point: Int,
    var gold: Int,
    @ColumnInfo(name = "categoryid") var categoryId: Long
)

/**
 * Extension function converting an entity activity to a domain activity.
 */
fun Activity.asDomainModel(): com.szte.wmm.greenkiwi.repository.domain.Activity {
    return com.szte.wmm.greenkiwi.repository.domain.Activity(
        activityId,
        title,
        description,
        thumbnailUrl,
        imageUrl,
        point,
        gold,
        Category.values().firstOrNull { it -> it.id == categoryId } ?: Category.OTHER
    )
}