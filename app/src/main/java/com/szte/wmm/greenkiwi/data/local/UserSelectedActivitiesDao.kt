package com.szte.wmm.greenkiwi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.szte.wmm.greenkiwi.data.local.model.UserSelectedActivity
import com.szte.wmm.greenkiwi.data.local.model.UserSelectedActivityWithDetails

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
    suspend fun getLatestXActivities(count: Int): List<UserSelectedActivity>

    @Query("""SELECT activities.activityid as activityId, title, point, gold, categoryid as categoryId, time_added as timeAdded
         FROM user_selected_activities
        INNER JOIN activities USING(activityid) ORDER BY id DESC LIMIT :count""")
    suspend fun getLatestXActivitiesWithDetails(count: Int): List<UserSelectedActivityWithDetails>

    @Query("DELETE FROM user_selected_activities")
    suspend fun deleteAllAddedActivities()
}
