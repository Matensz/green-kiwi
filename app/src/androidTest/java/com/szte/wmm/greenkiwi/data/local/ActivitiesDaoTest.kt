package com.szte.wmm.greenkiwi.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.szte.wmm.greenkiwi.data.local.model.Activity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Unit tests for ActivitiesDao.
 * @see ActivitiesDao
 */
@SmallTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ActivitiesDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var database: ApplicationDatabase
    private lateinit var activitiesDao: ActivitiesDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ApplicationDatabase::class.java).allowMainThreadQueries().build()
        activitiesDao = database.activitiesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Throws(Exception::class)
    @Test
    fun testInsertAllAndGetActivitiesShouldWorkCorrectly() = runBlockingTest {
        // given
        val activities = listOf(createActivity(1L), createActivity(2L))

        // when
        activitiesDao.insertAll(activities)
        val actual = activitiesDao.getActivities()

        // then
        assertThat(actual.size, `is`(equalTo(2)))
        assertThat(actual[0].activityId, `is`(equalTo(1L)))
        assertThat(actual[1].activityId, `is`(equalTo(2L)))
    }

    @Throws(Exception::class)
    @Test
    fun testGetActivityByIdShouldWorkCorrectly() = runBlockingTest {
        // given
        val activities = listOf(createActivity(1L), createActivity(2L))
        activitiesDao.insertAll(activities)

        // when
        val actual = activitiesDao.getActivityById(2L)

        // then
        assertNotNull(actual)
        assertThat(actual?.activityId, `is`(equalTo(2L)))
        assertThat(actual?.title, `is`(equalTo("activityTitle2")))
    }

    @Throws(Exception::class)
    @Test
    fun testInsertActivityShouldWorkCorrectly() = runBlockingTest {
        // given
        val activities = listOf(createActivity(1L), createActivity(2L))
        activitiesDao.insertAll(activities)
        val activity = createActivity(3L)

        // when
        activitiesDao.insertActivity(activity)
        val actual = activitiesDao.getActivities()

        // then
        assertThat(actual.size, `is`(equalTo(3)))
        assertThat(actual[0].activityId, `is`(equalTo(1L)))
        assertThat(actual[1].activityId, `is`(equalTo(2L)))
        assertThat(actual[2].activityId, `is`(equalTo(3L)))
    }

    private fun createActivity(id: Long): Activity {
        return Activity(id, "activityTitle${id}", "description${id}", "thumbnailUrl", "imageUrl", 1, 1, 1)
    }
}
