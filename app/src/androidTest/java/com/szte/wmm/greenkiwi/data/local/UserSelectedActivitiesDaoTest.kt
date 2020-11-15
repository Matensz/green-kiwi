package com.szte.wmm.greenkiwi.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.szte.wmm.greenkiwi.data.local.model.Activity
import com.szte.wmm.greenkiwi.data.local.model.UserSelectedActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Unit tests for UserSelectedActivitiesDao.
 * @see UserSelectedActivitiesDao
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class UserSelectedActivitiesDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var database: ApplicationDatabase
    private lateinit var activitiesDao: ActivitiesDao
    private lateinit var userSelectedActivitiesDao: UserSelectedActivitiesDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ApplicationDatabase::class.java).allowMainThreadQueries().build()
        activitiesDao = database.activitiesDao()
        userSelectedActivitiesDao = database.userSelectedActivitiesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Throws(Exception::class)
    @Test
    fun testInsertUserSelectedActivityAndGetLatestActivityShouldWorkCorrectly() = runBlockingTest {
        // given
        activitiesDao.insertAll(listOf(createActivity(1L), createActivity(2L)))
        val activity = createUserSelectedActivity(1L, 1000L)

        // when
        userSelectedActivitiesDao.insertUserSelectedActivity(activity)
        val actual = userSelectedActivitiesDao.getLatestActivity(1L)

        // then
        assertNotNull(actual)
        assertThat(actual?.activityId, `is`(equalTo(1L)))
        assertThat(actual?.timeAdded, `is`(equalTo(1000L)))
    }

    @Throws(Exception::class)
    @Test
    fun testGetLatestXActivitiesShouldWorkCorrectly() = runBlockingTest {
        // given
        activitiesDao.insertAll(listOf(createActivity(1L), createActivity(2L)))
        userSelectedActivitiesDao.insertUserSelectedActivity(createUserSelectedActivity(1L, 1000L))
        userSelectedActivitiesDao.insertUserSelectedActivity(createUserSelectedActivity(2L, 1001L))
        userSelectedActivitiesDao.insertUserSelectedActivity(createUserSelectedActivity(1L, 1002L))

        // when
        val actual = userSelectedActivitiesDao.getLatestXActivities(2)

        // then
        assertThat(actual.size, `is`(equalTo(2)))
        assertThat(actual[0].activityId, `is`(equalTo(1L)))
        assertThat(actual[0].timeAdded, `is`(equalTo(1002L)))
        assertThat(actual[1].activityId, `is`(equalTo(2L)))
    }

    @Throws(Exception::class)
    @Test
    fun testGetLatestXActivitiesWithDetailsShouldWorkCorrectly() = runBlockingTest {
        // given
        activitiesDao.insertAll(listOf(createActivity(1L), createActivity(2L)))
        userSelectedActivitiesDao.insertUserSelectedActivity(createUserSelectedActivity(1L, 1000L))
        userSelectedActivitiesDao.insertUserSelectedActivity(createUserSelectedActivity(2L, 1001L))
        userSelectedActivitiesDao.insertUserSelectedActivity(createUserSelectedActivity(1L, 1002L))

        // when
        val actual = userSelectedActivitiesDao.getLatestXActivitiesWithDetails(2)

        // then
        assertThat(actual.size, `is`(equalTo(2)))
        assertThat(actual[0].activityId, `is`(equalTo(1L)))
        assertThat(actual[0].timeAdded, `is`(equalTo(1002L)))
        assertThat(actual[0].title, `is`(equalTo("activityTitle1")))
        assertThat(actual[1].activityId, `is`(equalTo(2L)))
        assertThat(actual[1].timeAdded, `is`(equalTo(1001L)))
        assertThat(actual[1].title, `is`(equalTo("activityTitle2")))
    }

    @Throws(Exception::class)
    @Test
    fun testDeleteAllAddedActivitiesShouldWorkCorrectly() = runBlockingTest {
        // given
        activitiesDao.insertAll(listOf(createActivity(1L), createActivity(2L)))
        userSelectedActivitiesDao.insertUserSelectedActivity(createUserSelectedActivity(1L, 1000L))
        userSelectedActivitiesDao.insertUserSelectedActivity(createUserSelectedActivity(2L, 1001L))

        // when
        userSelectedActivitiesDao.deleteAllAddedActivities()
        val actual = userSelectedActivitiesDao.getLatestXActivities(2)

        // then
        assertThat(actual.size, `is`(equalTo(0)))
    }

    private fun createUserSelectedActivity(id: Long, timeAdded: Long): UserSelectedActivity {
        return UserSelectedActivity(activityId = id, timeAdded = timeAdded)
    }

    private fun createActivity(id: Long): Activity {
        return Activity(id, "activityTitle${id}", "description${id}", "thumbnailUrl", "imageUrl", 1, 1, 1)
    }
}
