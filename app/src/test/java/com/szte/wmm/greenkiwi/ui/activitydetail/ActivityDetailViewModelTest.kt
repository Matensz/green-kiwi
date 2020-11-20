package com.szte.wmm.greenkiwi.ui.activitydetail

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.szte.wmm.greenkiwi.CoroutineTestRule
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.Category
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

/**
 * Unit test for ActivityDetailViewModel.
 * @see ActivityDetailViewModel
 */
@ExperimentalCoroutinesApi
@Config(sdk = [28])
@RunWith(AndroidJUnit4::class)
class ActivityDetailViewModelTest {

    companion object {
        private const val ACTIVITY_ID = 1L
        private const val CURRENT_TIME = 1605463200000L //2020-11-15 19:00:00
        private const val ONE_DAY_BEFORE_CURRENT_TIME = 1605376800000 //2020-11-14 19:00:00
        private val ACTIVITY = Activity(ACTIVITY_ID, "title", "description", "thumbnailUrl", "imageUrl", 1, 1, Category.WATER_AND_ENERGY)
        private val USER_SELECTED_ACTIVITY = UserSelectedActivity(activityId = ACTIVITY_ID, timeAdded = CURRENT_TIME)
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var userSelectedActivitiesRepository: UserSelectedActivitiesRepository

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var application: Application
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var underTest: ActivityDetailViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        application = ApplicationProvider.getApplicationContext()
        sharedPreferences = application.getSharedPreferences(application.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        userSelectedActivitiesRepository.stub {
            onBlocking { getLatestActivity(ACTIVITY_ID) }.doReturn(USER_SELECTED_ACTIVITY)
        }
        underTest = ActivityDetailViewModel(ACTIVITY, userSelectedActivitiesRepository, ApplicationProvider.getApplicationContext(), testDispatcher)
    }

    @Test
    fun `test addActivity() should set last added date properly`() = coroutineTestRule.runBlockingTest {
        // given
        // empty observer for testing
        val observer = Observer<Long?> {}
        try {

            // observe the live data
            underTest.lastAddedDate.observeForever(observer)

            // when
            underTest.addActivity(ACTIVITY_ID, CURRENT_TIME)

            // then
            val actualValue = underTest.lastAddedDate.value
            assertNotNull(actualValue)
            assertThat(actualValue, `is`(equalTo(CURRENT_TIME)))
            verifyBlocking(userSelectedActivitiesRepository, times(1)) { insertUserSelectedActivity(USER_SELECTED_ACTIVITY) }
            verify(userSelectedActivitiesRepository, times(2)).getLatestActivity(ACTIVITY_ID)
            verifyNoMoreInteractions(userSelectedActivitiesRepository)
        } finally {
            // cleaning up observer
            underTest.lastAddedDate.removeObserver(observer)
        }
    }

    @Test
    fun `test updatePlayerValue() should update value in shared preferences`() = coroutineTestRule.runBlockingTest {
        // given
        val userPointsKey = application.getString(R.string.saved_user_points_key)
        sharedPreferences.edit().putLong(userPointsKey, 10L).apply()

        // when
        underTest.updatePlayerValue(10, R.integer.default_starting_point, R.string.saved_user_points_key)

        // then
        val userPoints = sharedPreferences.getLong(userPointsKey, 0L)
        assertThat(userPoints, `is`(equalTo(20L)))
    }

    @Test
    fun `test getUpdatedDailyCounter() should update latest save date and counter value in shared preferences when last saved date is before current date`() =
        coroutineTestRule.runBlockingTest {
            // given
            val dailyActivityCounterKey = application.getString(R.string.daily_activity_counter_key)
            val lastSavedDateKey = application.getString(R.string.last_saved_activity_date_key)
            with(sharedPreferences.edit()) {
                putInt(dailyActivityCounterKey, 3)
                putLong(lastSavedDateKey, ONE_DAY_BEFORE_CURRENT_TIME)
                apply()
            }

            // when
            underTest.getUpdatedDailyCounter(CURRENT_TIME)

            // then
            val dailyCounter = sharedPreferences.getInt(dailyActivityCounterKey, 0)
            val lastSavedDate = sharedPreferences.getLong(lastSavedDateKey, 0L)
            assertThat(dailyCounter, `is`(equalTo(1)))
            assertThat(lastSavedDate, `is`(equalTo(CURRENT_TIME)))
        }

    @Test
    fun `test getUpdatedDailyCounter() should update counter value in shared preferences when last saved date is same as current date`() =
        coroutineTestRule.runBlockingTest {
            // given
            val dailyActivityCounterKey = application.getString(R.string.daily_activity_counter_key)
            val lastSavedDateKey = application.getString(R.string.last_saved_activity_date_key)
            with(sharedPreferences.edit()) {
                putInt(dailyActivityCounterKey, 1)
                putLong(lastSavedDateKey, CURRENT_TIME)
                apply()
            }

            // when
            underTest.getUpdatedDailyCounter(CURRENT_TIME)

            // then
            val dailyCounter = sharedPreferences.getInt(dailyActivityCounterKey, 0)
            val lastSavedDate = sharedPreferences.getLong(lastSavedDateKey, 0L)
            assertThat(dailyCounter, `is`(equalTo(2)))
            assertThat(lastSavedDate, `is`(equalTo(CURRENT_TIME)))
        }
}
