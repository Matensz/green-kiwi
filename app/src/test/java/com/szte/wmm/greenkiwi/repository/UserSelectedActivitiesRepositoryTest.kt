package com.szte.wmm.greenkiwi.repository

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.szte.wmm.greenkiwi.CURRENT_TIME
import com.szte.wmm.greenkiwi.createUserSelectedActivity
import com.szte.wmm.greenkiwi.createUserSelectedActivityWithDetails
import com.szte.wmm.greenkiwi.data.local.UserSelectedActivitiesDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit tests for UserSelectedActivitiesRepository.
 * @see UserSelectedActivitiesRepository
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserSelectedActivitiesRepositoryTest {

    @Mock
    private lateinit var userSelectedActivitiesDao: UserSelectedActivitiesDao

    @Test
    fun `test getLatestActivity() should delegate to dao method`() {
        // given
        `when`(userSelectedActivitiesDao.getLatestActivity(1L)).thenReturn(createDatabaseActivity(1L, CURRENT_TIME))
        val underTest = UserSelectedActivitiesRepository(userSelectedActivitiesDao)
        val expected = createUserSelectedActivity(1L, CURRENT_TIME)

        // when
        val actual = underTest.getLatestActivity(1L)

        // then
        assertNotNull(actual)
        assertThat(actual, `is`(equalTo(expected)))
    }

    @Test
    fun `test getLatestXActivities() should delegate to dao method`() = runBlockingTest {
        // given
        userSelectedActivitiesDao.stub {
            onBlocking { getLatestXActivities(2) }.doReturn(listOf(createDatabaseActivity(2L, CURRENT_TIME + 1), createDatabaseActivity(1L, CURRENT_TIME)))
        }
        val underTest = UserSelectedActivitiesRepository(userSelectedActivitiesDao)
        val expected = listOf(createUserSelectedActivity(2L, CURRENT_TIME + 1), createUserSelectedActivity(1L, CURRENT_TIME))

        // when
        val actual = underTest.getLatestXActivities(2)

        // then
        assertThat(actual.size, `is`(equalTo(2)))
        assertThat(actual[0], `is`(equalTo(expected[0])))
        assertThat(actual[1], `is`(equalTo(expected[1])))
    }

    @Test
    fun `test getLatestXActivitiesWithDetails() should delegate to dao method`() = runBlockingTest {
        // given
        userSelectedActivitiesDao.stub {
            onBlocking { getLatestXActivitiesWithDetails(2) }.doReturn(listOf(createDatabaseActivityWithDetails(2L), createDatabaseActivityWithDetails(1L)))
        }
        val underTest = UserSelectedActivitiesRepository(userSelectedActivitiesDao)
        val expected = listOf(createUserSelectedActivityWithDetails(2L), createUserSelectedActivityWithDetails(1L))

        // when
        val actual = underTest.getLatestXActivitiesWithDetails(2)

        // then
        assertThat(actual.size, `is`(equalTo(2)))
        assertThat(actual[0], `is`(equalTo(expected[0])))
        assertThat(actual[1], `is`(equalTo(expected[1])))
    }

    @Test
    fun `test insertUserSelectedActivity() should delegate to dao method`() = runBlockingTest {
        // given
        val underTest = UserSelectedActivitiesRepository(userSelectedActivitiesDao)

        // when
        underTest.insertUserSelectedActivity(createUserSelectedActivity(1L, CURRENT_TIME))

        // then
        verifyBlocking(userSelectedActivitiesDao, times(1)){ insertUserSelectedActivity(createDatabaseActivity(1L, CURRENT_TIME)) }
        verifyNoMoreInteractions(userSelectedActivitiesDao)
    }

    @Test
    fun `test deleteAllAddedActivities should delegate to dao method`() = runBlockingTest {
        // given
        val underTest = UserSelectedActivitiesRepository(userSelectedActivitiesDao)

        // when
        underTest.deleteAllAddedActivities()

        // then
        verifyBlocking(userSelectedActivitiesDao, times(1)){ deleteAllAddedActivities() }
        verifyNoMoreInteractions(userSelectedActivitiesDao)
    }

    private fun createDatabaseActivity(id: Long, timeAdded: Long): com.szte.wmm.greenkiwi.data.local.model.UserSelectedActivity {
        return com.szte.wmm.greenkiwi.data.local.model.UserSelectedActivity(id, id, timeAdded)
    }

    private fun createDatabaseActivityWithDetails(id: Long): com.szte.wmm.greenkiwi.data.local.model.UserSelectedActivityWithDetails {
        return com.szte.wmm.greenkiwi.data.local.model.UserSelectedActivityWithDetails(id, "title", 1, 1, 1L, CURRENT_TIME)
    }
}
