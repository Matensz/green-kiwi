package com.szte.wmm.greenkiwi.ui.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.szte.wmm.greenkiwi.CoroutineTestRule
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Category
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivityWithDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit tests for HistoryViewModel.
 * @see HistoryViewModel
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HistoryViewModelTest {

    companion object {
        private const val HISTORY_LIST_LENGTH = 15
        private const val DAILY_COUNTER_MAX_VALUE = 3
        private const val CURRENT_TIME = 1605463200000L //2020-11-15 19:00:00
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var userSelectedActivitiesRepository: UserSelectedActivitiesRepository

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var underTest: HistoryViewModel

    @Test
    fun `test init should set up live data with repository calls correctly`() = runBlockingTest {
        // given
        userSelectedActivitiesRepository.stub {
            onBlocking { getLatestXActivities(DAILY_COUNTER_MAX_VALUE) }.doReturn(listOf(createUserSelectedActivity(2L), createUserSelectedActivity(1L)))
        }
        userSelectedActivitiesRepository.stub {
            onBlocking { getLatestXActivitiesWithDetails(HISTORY_LIST_LENGTH) }.doReturn(listOf(createUserSelectedActivityWithDetails(2L), createUserSelectedActivityWithDetails(1L)))
        }

        // when
        underTest = HistoryViewModel(userSelectedActivitiesRepository, testDispatcher)

        // then
        verifyBlocking(userSelectedActivitiesRepository, times(1)) { getLatestXActivities(DAILY_COUNTER_MAX_VALUE) }
        verifyBlocking(userSelectedActivitiesRepository, times(1)) { getLatestXActivitiesWithDetails(HISTORY_LIST_LENGTH) }
        verifyNoMoreInteractions(userSelectedActivitiesRepository)
    }

    private fun createUserSelectedActivity(id: Long): UserSelectedActivity {
        return UserSelectedActivity(activityId = id, timeAdded = CURRENT_TIME)
    }

    private fun createUserSelectedActivityWithDetails(id: Long): UserSelectedActivityWithDetails {
        return UserSelectedActivityWithDetails(id, "title", 1, 1, Category.WATER_AND_ENERGY, CURRENT_TIME.toString())
    }
}
