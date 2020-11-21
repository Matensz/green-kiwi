package com.szte.wmm.greenkiwi.ui.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.szte.wmm.greenkiwi.CURRENT_TIME
import com.szte.wmm.greenkiwi.CoroutineTestRule
import com.szte.wmm.greenkiwi.createUserSelectedActivity
import com.szte.wmm.greenkiwi.createUserSelectedActivityWithDetails
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit test for HistoryViewModel.
 * @see HistoryViewModel
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HistoryViewModelTest {

    companion object {
        private const val HISTORY_LIST_LENGTH = 15
        private const val DAILY_COUNTER_MAX_VALUE = 3
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
            onBlocking {
                getLatestXActivities(DAILY_COUNTER_MAX_VALUE)
            }.doReturn(listOf(createUserSelectedActivity(2L, CURRENT_TIME), createUserSelectedActivity(1L, CURRENT_TIME)))
        }
        userSelectedActivitiesRepository.stub {
            onBlocking {
                getLatestXActivitiesWithDetails(HISTORY_LIST_LENGTH)
            }.doReturn(listOf(createUserSelectedActivityWithDetails(2L), createUserSelectedActivityWithDetails(1L)))
        }

        // when
        underTest = HistoryViewModel(userSelectedActivitiesRepository, testDispatcher)

        // then
        verifyBlocking(userSelectedActivitiesRepository, times(1)) { getLatestXActivities(DAILY_COUNTER_MAX_VALUE) }
        verifyBlocking(userSelectedActivitiesRepository, times(1)) { getLatestXActivitiesWithDetails(HISTORY_LIST_LENGTH) }
        verifyNoMoreInteractions(userSelectedActivitiesRepository)
    }
}
