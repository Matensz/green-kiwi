package com.szte.wmm.greenkiwi.ui.history

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.TestActivitiesRepository
import com.szte.wmm.greenkiwi.repository.TestUserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.Category
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import com.szte.wmm.greenkiwi.util.ServiceLocator
import com.szte.wmm.greenkiwi.util.atPosition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.startsWith
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test for testing HistoryFragment.
 * @see HistoryFragment
 */
@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HistoryFragmentTest {

    private lateinit var testActivitiesRepository: TestActivitiesRepository
    private lateinit var testUserSelectedActivitiesRepository: TestUserSelectedActivitiesRepository

    @Before
    fun initRepository() {
        testActivitiesRepository = TestActivitiesRepository()
        testActivitiesRepository.addActivities(listOf(
                createActivity(1L, Category.WATER_AND_ENERGY),
                createActivity(2L, Category.WATER_AND_ENERGY),
                createActivity(3L, Category.OTHER)
        ))
        testUserSelectedActivitiesRepository = TestUserSelectedActivitiesRepository()
        ServiceLocator.activitiesRepository = testActivitiesRepository
        ServiceLocator.userSelectedActivitiesRepository = testUserSelectedActivitiesRepository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepositories()
    }

    @Test
    fun testHistoryListIsDisplayed() = runBlockingTest {
        // given
        testUserSelectedActivitiesRepository.addUserSelectedActivities(listOf(createUserSelectedActivity(1L), createUserSelectedActivity(2L)))

        // when
        launchFragmentInContainer<HistoryFragment>(Bundle(), R.style.DefaultAppTheme)

        // then
        onView(withId(R.id.pet_image)).check(matches(isDisplayed()))
        onView(withId(R.id.daily_activity_notification)).check(matches(isDisplayed()))
        onView(withId(R.id.daily_activity_notification)).check(matches(withText(startsWith("Mai rögzített tevékenységek: 2/3"))))
        onView(withId(R.id.activity_history_list)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_history_list)).check(matches(atPosition(0, withText("Korábban hozzáadott tevékenységeid:"))))
        onView(withId(R.id.activity_history_list)).check(matches(atPosition(1, hasDescendant(withText("title")))))
        onView(withId(R.id.activity_history_list)).check(matches(atPosition(2, hasDescendant(withText("title")))))
    }

    @Test
    fun testHistoryListIsDisplayedWithCompletedDailyActivities() = runBlockingTest {
        // given
        testUserSelectedActivitiesRepository.addUserSelectedActivities(listOf(createUserSelectedActivity(1L), createUserSelectedActivity(2L), createUserSelectedActivity(3L)))

        // when
        launchFragmentInContainer<HistoryFragment>(Bundle(), R.style.DefaultAppTheme)

        // then
        onView(withId(R.id.pet_image)).check(matches(isDisplayed()))
        onView(withId(R.id.daily_activity_notification)).check(matches(isDisplayed()))
        onView(withId(R.id.daily_activity_notification)).check(matches(withText(startsWith("Gratulálunk! Máris 3 tevékenységet rögzítettél ma!"))))
        onView(withId(R.id.activity_history_list)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_history_list)).check(matches(atPosition(0, withText("Korábban hozzáadott tevékenységeid:"))))
        onView(withId(R.id.activity_history_list)).check(matches(atPosition(1, hasDescendant(withText("title")))))
        onView(withId(R.id.activity_history_list)).check(matches(atPosition(2, hasDescendant(withText("title")))))
        onView(withId(R.id.activity_history_list)).check(matches(atPosition(3, hasDescendant(withText("title")))))
    }

    private fun createUserSelectedActivity(id: Long): UserSelectedActivity {
        return UserSelectedActivity(id, id, System.currentTimeMillis())
    }

    private fun createActivity(id: Long, category: Category): Activity {
        return Activity(id, "title${id}", "description${id}", "thumbnailUrl", "imageUrl", 1, 1, category)
    }
}
