package com.szte.wmm.greenkiwi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.szte.wmm.greenkiwi.data.local.model.Activity

/**
 * Room Dao object for the Activity related queries.
 */
@Dao
interface ActivitiesDao {

    @Query("SELECT * FROM Activities")
    suspend fun getActivities(): List<Activity>

    @Query("SELECT * FROM Activities WHERE activityid = :activityId")
    fun getActivityById(activityId: Long): Activity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivity(activity: Activity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Activity>)
}