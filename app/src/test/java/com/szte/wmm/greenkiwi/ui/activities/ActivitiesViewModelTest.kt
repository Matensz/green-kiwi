package com.szte.wmm.greenkiwi.ui.activities

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.szte.wmm.greenkiwi.CoroutineTestRule
import com.szte.wmm.greenkiwi.createActivity
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.Category
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks

/**
 * Unit test for ActivitiesViewModel.
 * @see ActivitiesViewModel
 */
@ExperimentalCoroutinesApi
class ActivitiesViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var activitiesRepository: ActivitiesRepository

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var underTest: ActivitiesViewModel

    @Before
    fun setUp() {
        initMocks(this)
        activitiesRepository.stub {
            onBlocking { getActivities() }.doReturn(listOf(createActivity(1L, Category.WATER_AND_ENERGY), createActivity(2L, Category.OTHER)))
        }
        underTest = ActivitiesViewModel(activitiesRepository, testDispatcher)
    }

    @Test
    fun `test onFilterChanged() should update filtered activities properly`() = coroutineTestRule.runBlockingTest {
        // given
        // empty observer for testing
        val observer = Observer<List<Activity>> {}
        try {

            // observe the live data
            underTest.filteredActivities.observeForever(observer)

            // when
            underTest.onFilterChanged(Category.WATER_AND_ENERGY.name, true)

            // then
            val actualValue = underTest.filteredActivities.value
            assertThat(actualValue?.size, `is`(equalTo(1)))
            assertThat(actualValue?.get(0)?.activityId, `is`(equalTo(1L)))
            repositoryVerifications()
        } finally {
            // cleaning up observer
            underTest.filteredActivities.removeObserver(observer)
        }
    }

    @Test
    fun `test displayActivityDetails() should set up navigation value to activity`() = coroutineTestRule.runBlockingTest {
        // given
        val expectedValue = createActivity(1L, Category.WATER_AND_ENERGY)
        // empty observer for testing
        val observer = Observer<Activity?> {}
        try {

            // observe the live data
            underTest.navigateToSelectedActivity.observeForever(observer)

            // when
            underTest.displayActivityDetails(expectedValue)

            // then
            val actualValue = underTest.navigateToSelectedActivity.value
            assertThat(actualValue?.activityId, `is`(equalTo(expectedValue.activityId)))
            repositoryVerifications()
        } finally {
            // cleaning up observer
            underTest.navigateToSelectedActivity.removeObserver(observer)
        }
    }

    @Test
    fun `test displayActivityDetailsComplete() should set up navigation value null`() = coroutineTestRule.runBlockingTest {
        // given
        // empty observer for testing
        val observer = Observer<Activity?> {}
        try {

            // observe the live data
            underTest.navigateToSelectedActivity.observeForever(observer)

            // when
            underTest.displayActivityDetailsComplete()

            // then
            val actualValue = underTest.navigateToSelectedActivity.value
            assertNull(actualValue)
            repositoryVerifications()
        } finally {
            // cleaning up observer
            underTest.navigateToSelectedActivity.removeObserver(observer)
        }
    }

    private fun repositoryVerifications() {
        verifyBlocking(activitiesRepository, times(1)){ getActivities() }
        verifyNoMoreInteractions(activitiesRepository)
    }
}
