package com.szte.wmm.greenkiwi.ui.activitydetail

import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.TestUserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.Category
import com.szte.wmm.greenkiwi.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

/**
 * Instrumented test for testing ActivityDetailFragment.
 * @see ActivityDetailFragment
 */
@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ActivityDetailFragmentTest {

    private lateinit var testRepository: TestUserSelectedActivitiesRepository

    @Before
    fun initRepository() {
        testRepository = TestUserSelectedActivitiesRepository()
        ServiceLocator.userSelectedActivitiesRepository = testRepository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepositories()
    }

    @Test
    fun testActivityDetailsAreDisplayed() {
        // given
        val activity = Activity(1L, "Activity title", "This is a long activity description", "thumbnailurl", "imageUrl", 1, 1, Category.OTHER)
        val bundle = ActivityDetailFragmentArgs(activity).toBundle()

        // when
        launchFragmentInContainer<ActivityDetailFragment>(bundle, R.style.DefaultAppTheme)

        // then
        onView(withId(R.id.activity_title)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_title)).check(matches(withText("Activity title")))
        onView(withId(R.id.activity_description)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_description)).check(matches(withText("This is a long activity description")))
        onView(withId(R.id.activity_history_title)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_history_title)).check(matches(withText("Legutóbb hozzáadva")))
        onView(withId(R.id.activity_history_date)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_history_date)).check(matches(withText("Még nem rögzítettél ilyen tevékenységet")))
        onView(withId(R.id.activity_points_title)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_points_title)).check(matches(withText("Ez a tevékenység 1 pontot ér")))
        onView(withId(R.id.activity_gold_title)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_gold_title)).check(matches(withText("Ez a tevékenység 1 aranyat ér")))
    }

    @Suppress("UnsafeCallOnNullableType")
    @Test
    fun testClickingBackButtonShouldNavigateBackToActivitiesList() {
        // given
        val activity = Activity(1L, "Activity title", "This is a long activity description", "thumbnailurl", "imageUrl", 1, 1, Category.OTHER)
        val bundle = ActivityDetailFragmentArgs(activity).toBundle()
        val scenario = launchFragmentInContainer<ActivityDetailFragment>(bundle, R.style.DefaultAppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // when
        onView(allOf(instanceOf(AppCompatImageButton::class.java), withParent(withId(R.id.toolbar)))).perform(click())

        // then
        verify(navController).navigateUp()
    }

    @Test
    fun testClickingAddFabShouldUpdateActivityHistoryValue() = runBlockingTest {
        // given
        val activity = Activity(1L, "Activity title", "This is a long activity description", "thumbnailurl", "imageUrl", 1, 1, Category.OTHER)
        val bundle = ActivityDetailFragmentArgs(activity).toBundle()
        launchFragmentInContainer<ActivityDetailFragment>(bundle, R.style.DefaultAppTheme)

        // when
        onView(withId(R.id.fab)).perform(click())

        // then
        onView(withId(R.id.activity_history_date)).check(matches(withText("2099.12.31")))
    }
}
