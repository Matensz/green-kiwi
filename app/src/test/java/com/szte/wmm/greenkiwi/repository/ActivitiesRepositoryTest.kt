package com.szte.wmm.greenkiwi.repository

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.szte.wmm.greenkiwi.createActivity
import com.szte.wmm.greenkiwi.data.local.ActivitiesDao
import com.szte.wmm.greenkiwi.repository.domain.Category
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit tests for ActivitiesRepository.
 * @see ActivitiesRepository
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ActivitiesRepositoryTest {

    @Mock
    private lateinit var activitiesDao: ActivitiesDao

    @Test
    fun `test getActivities() should delegate to dao method`() = runBlockingTest {
        // given
        activitiesDao.stub {
            onBlocking { getActivities() }.doReturn(listOf(createDatabaseActivity(1L), createDatabaseActivity(2L)))
        }
        val expectedResult = listOf(createActivity(1L, Category.WATER_AND_ENERGY), createActivity(2L, Category.WATER_AND_ENERGY))
        val underTest = ActivitiesRepository(activitiesDao)

        // when
        val actual = underTest.getActivities()

        // then
        assertThat(actual.size, `is`(equalTo(2)))
        assertThat(actual, `is`(equalTo(expectedResult)))
        verifyBlocking(activitiesDao, times(1), ActivitiesDao::getActivities)
        verifyNoMoreInteractions(activitiesDao)
    }

    private fun createDatabaseActivity(id: Long): com.szte.wmm.greenkiwi.data.local.model.Activity {
        return com.szte.wmm.greenkiwi.data.local.model.Activity(id, "title", "description", "thumbnailUrl", "imageUrl", 1, 1, 1L)
    }
}
