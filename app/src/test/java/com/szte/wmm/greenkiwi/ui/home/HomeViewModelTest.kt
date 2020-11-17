package com.szte.wmm.greenkiwi.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.szte.wmm.greenkiwi.CoroutineTestRule
import com.szte.wmm.greenkiwi.createActivity
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Category
import com.szte.wmm.greenkiwi.ui.home.context.HomeDataContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

/**
 * Unit test for HomeViewModel.
 * @see HomeViewModel
 */
@ExperimentalCoroutinesApi
@Config(sdk = [28])
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var activitiesRepository: ActivitiesRepository

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var underTest: HomeViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        activitiesRepository.stub {
            onBlocking { getActivities() }.doReturn(listOf(createActivity(1L, Category.WATER_AND_ENERGY), createActivity(2L, Category.OTHER)))
        }
        val homeDataContext = HomeDataContext(100L, 25)
        underTest = HomeViewModel(homeDataContext, activitiesRepository, ApplicationProvider.getApplicationContext(), testDispatcher)
    }

    @Test
    fun `test feedPet() should set feed button visible to false`() = coroutineTestRule.runBlockingTest {
        // given
        // empty observer for testing
        val observer = Observer<Boolean> {}
        try {

            // observe the live data
            underTest.feedButtonVisible.observeForever(observer)

            // when
            underTest.feedPet()

            // then
            val actualValue = underTest.feedButtonVisible.value
            assertFalse(actualValue?: true)
            repositoryVerifications()
        } finally {
            // cleaning up observer
            underTest.feedButtonVisible.removeObserver(observer)
        }
    }

    @Test
    fun `test navigateToShop() should set up navigation value to true`() = coroutineTestRule.runBlockingTest {
        // given
        // empty observer for testing
        val observer = Observer<Boolean> {}
        try {

            // observe the live data
            underTest.navigateToShop.observeForever(observer)

            // when
            underTest.navigateToShop()

            // then
            val actualValue = underTest.navigateToShop.value
            assertTrue(actualValue?:false)
        } finally {
            // cleaning up observer
            underTest.navigateToShop.removeObserver(observer)
        }
    }

    @Test
    fun `test navigateToShopComplete() should set up navigation value to null`() = coroutineTestRule.runBlockingTest {
        // given
        // empty observer for testing
        val observer = Observer<Boolean> {}
        try {

            // observe the live data
            underTest.navigateToShop.observeForever(observer)

            // when
            underTest.navigateToShopComplete()

            // then
            val actualValue = underTest.navigateToShop.value
            Assert.assertNull(actualValue)
            repositoryVerifications()
        } finally {
            // cleaning up observer
            underTest.navigateToShop.removeObserver(observer)
        }
    }

    private fun repositoryVerifications() {
        verifyBlocking(activitiesRepository, times(1)){ getActivities() }
        verifyNoMoreInteractions(activitiesRepository)
    }
}
