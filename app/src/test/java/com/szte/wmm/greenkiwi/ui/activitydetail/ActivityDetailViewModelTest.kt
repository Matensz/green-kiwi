package com.szte.wmm.greenkiwi.ui.activitydetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.szte.wmm.greenkiwi.CoroutineTestRule
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit test for ActivityDetailViewModel.
 * @see ActivityDetailViewModel
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ActivityDetailViewModelTest {

    companion object {
        private const val ACTIVITY_ID = 1L
        private const val CURRENT_TIME = 1605463200000L //2020-11-15 19:00:00
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

    private lateinit var underTest: ActivityDetailViewModel

    @Test
    fun `test addActivity() should set last added date properly`() = coroutineTestRule.runBlockingTest {
        // given
        userSelectedActivitiesRepository.stub {
            onBlocking { getLatestActivity(ACTIVITY_ID) }.doReturn(USER_SELECTED_ACTIVITY)
        }
        underTest = ActivityDetailViewModel(ACTIVITY, userSelectedActivitiesRepository, testDispatcher)
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
}
