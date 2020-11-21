package com.szte.wmm.greenkiwi.ui.activities

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.material.chip.Chip
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.TestActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.Category
import com.szte.wmm.greenkiwi.util.ServiceLocator
import com.szte.wmm.greenkiwi.util.atPosition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

/**
 * Instrumented test for testing ActivitiesFragment.
 * @see ActivitiesFragment
 */
@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ActivitiesFragmentTest {

    private lateinit var testRepository: TestActivitiesRepository

    @Before
    fun initRepository() {
        testRepository = TestActivitiesRepository()
        testRepository.addActivities(listOf(createActivity(1L, Category.WATER_AND_ENERGY), createActivity(2L, Category.WATER_AND_ENERGY), createActivity(3L, Category.OTHER)))
        ServiceLocator.activitiesRepository = testRepository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepositories()
    }

    @Test
    fun testActivityListIsDisplayed() {
        // given

        // when
        launchFragmentInContainer<ActivitiesFragment>(Bundle(), R.style.DefaultAppTheme)

        // then
        onView(withId(R.id.categories)).check(matches(isDisplayed()))
        onView(withId(R.id.activities_list)).check(matches(isDisplayed()))
        onView(withId(R.id.activities_list)).check(matches(atPosition(0, hasDescendant(withText("title1")))))
        onView(withId(R.id.activities_list)).check(matches(atPosition(1, hasDescendant(withText("title2")))))
        onView(withId(R.id.activities_list)).check(matches(atPosition(2, hasDescendant(withText("title3")))))
    }

    @Test
    fun testClickingOnACategoryChipShouldFilterActivityList() {
        // given
        launchFragmentInContainer<ActivitiesFragment>(Bundle(), R.style.DefaultAppTheme)

        // when
        onView(allOf(`is`(instanceOf(Chip::class.java)), withText("Víz és energia"))).perform(click())


        // then
        onView(withId(R.id.categories)).check(matches(isDisplayed()))
        onView(withId(R.id.activities_list)).check(matches(isDisplayed()))
        onView(withId(R.id.activities_list)).check(matches(atPosition(0, hasDescendant(withText("title1")))))
        onView(withId(R.id.activities_list)).check(matches(atPosition(1, hasDescendant(withText("title2")))))
        onView(withId(R.id.activities_list)).check(matches(hasChildCount(2)))
    }

    @Suppress("UnsafeCallOnNullableType")
    @Test
    fun testClickingOnAListItemShouldNavigateToActivityDetailsPageForThatActivity() {
        // given
        val scenario = launchFragmentInContainer<ActivitiesFragment>(Bundle(), R.style.DefaultAppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // when
        onView(withId(R.id.activities_list)).perform(actionOnItemAtPosition<ActivityAdapter.ActivityViewHolder>(0, click()))

        // then
        verify(navController)
            .navigate(ActivitiesFragmentDirections.actionNavigationActivitiesToNavigationActivityDetail(createActivity(1L, Category.WATER_AND_ENERGY)))
    }

    private fun createActivity(id: Long, category: Category): Activity {
        return Activity(id, "title${id}", "description${id}", "thumbnailUrl", "imageUrl", 1, 1, category)
    }
}
