package com.szte.wmm.greenkiwi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.szte.wmm.greenkiwi.data.local.model.UserSelectedActivity

/**
 * Room Dao object for the UserSelectedActivity related queries.
 */
@Dao
interface UserSelectedActivitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSelectedActivity(userSelectedActivity: UserSelectedActivity)

    @Query("SELECT * FROM user_selected_activities WHERE activityid = :activityId ORDER BY id DESC LIMIT 1")
    fun getLatestActivity(activityId: Long): UserSelectedActivity?

    @Query("SELECT * FROM user_selected_activities ORDER BY id DESC LIMIT :count")
    fun getLatestXActivities(count: Int): List<UserSelectedActivity>

    @Query("DELETE FROM user_selected_activities")
    suspend fun deleteAllAddedActivities()
}
