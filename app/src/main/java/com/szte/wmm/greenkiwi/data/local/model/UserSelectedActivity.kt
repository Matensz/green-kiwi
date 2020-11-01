package com.szte.wmm.greenkiwi.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Database entity representing an activity selected by the user.
 */
@Entity(
    tableName = "user_selected_activities",
    foreignKeys = [
        androidx.room.ForeignKey(entity = Activity::class, parentColumns = ["activityid"], childColumns = ["activityid"])
    ],
    indices = [Index("activityid")])
data class UserSelectedActivity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    @ColumnInfo(name = "activityid") var activityId: Long,
    @ColumnInfo(name = "time_added") var timeAdded: Long
)

/**
 * Extension function converting an entity activity to a domain activity.
 */
fun UserSelectedActivity.asDomainModel(): com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity {
    return com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity(
        id,
        activityId,
        timeAdded
    )
}