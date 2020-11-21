package com.szte.wmm.greenkiwi.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

/**
 * Instrumented test for testing HomeFragment.
 * @see HomeFragment
 */
@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    private lateinit var context: Context
    private lateinit var sharedPrefs: SharedPreferences

    @Before
    fun initRepository() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        sharedPrefs.edit().remove(context.getString(R.string.saved_user_points_key)).apply()
        sharedPrefs.edit().remove(context.getString(R.string.saved_user_gold_key)).apply()
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepositories()
        sharedPrefs.edit().remove(context.getString(R.string.saved_user_points_key)).apply()
        sharedPrefs.edit().remove(context.getString(R.string.saved_user_gold_key)).apply()
    }

    @Test
    fun testHomeViewIsCorrectlyDisplayed() {
        // given
        sharedPrefs.edit().putLong(context.getString(R.string.saved_user_points_key), 10L).apply()
        sharedPrefs.edit().putLong(context.getString(R.string.saved_user_gold_key), 10L).apply()

        // when
        launchFragmentInContainer<HomeFragment>(Bundle(), R.style.DefaultAppTheme)

        // then
        onView(withId(R.id.gold_image)).check(matches(isDisplayed()))
        onView(withId(R.id.player_gold_text)).check(matches(isDisplayed()))
        onView(withId(R.id.player_gold_text)).check(matches(withText("10")))
        onView(withId(R.id.pet_nickname_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.pet_nickname_edit)).check(matches(not(isDisplayed())))
        onView(withId(R.id.pet_nickname_button)).check(matches(not(isDisplayed())))
        onView(withId(R.id.pet_image)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_exp_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.collected_exp_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.exp_text)).check(matches(isDisplayed()))
        onView(withId(R.id.exp_text)).check(matches(withText("10/50 XP")))
        onView(withId(R.id.empty_hunger_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.filled_hunger_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.hunger_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.feed_pet_button)).check(matches(not(isDisplayed())))
        onView(withId(R.id.player_level_text)).check(matches(isDisplayed()))
        onView(withId(R.id.player_level_text)).check(matches(withText("1. szint")))
    }

    @Test
    fun testPetNickNameAndHungerBarShouldBeVisibleWhenPetLevelIsMoreThanOrEqualToFive() {
        // given
        sharedPrefs.edit().putLong(context.getString(R.string.saved_user_points_key), 700L).apply()
        sharedPrefs.edit().putLong(context.getString(R.string.saved_user_gold_key), 100L).apply()

        // when
        launchFragmentInContainer<HomeFragment>(Bundle(), R.style.DefaultAppTheme)

        // then
        onView(withId(R.id.gold_image)).check(matches(isDisplayed()))
        onView(withId(R.id.player_gold_text)).check(matches(isDisplayed()))
        onView(withId(R.id.player_gold_text)).check(matches(withText("100")))
        onView(withId(R.id.pet_nickname_text)).check(matches(withText("Adj egy becenevet a kiwidnek")))
        onView(withId(R.id.pet_nickname_edit)).check(matches(not(isDisplayed())))
        onView(withId(R.id.pet_nickname_button)).check(matches(not(isDisplayed())))
        onView(withId(R.id.pet_image)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_exp_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.collected_exp_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.exp_text)).check(matches(isDisplayed()))
        onView(withId(R.id.exp_text)).check(matches(withText("200/250 XP")))
        onView(withId(R.id.empty_hunger_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.filled_hunger_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.hunger_text)).check(matches(isDisplayed()))
        onView(withId(R.id.player_level_text)).check(matches(isDisplayed()))
        onView(withId(R.id.player_level_text)).check(matches(withText("5. szint")))
    }

    @Test
    fun testClickingOnPetNickNameShouldMakePetNicknameEditAndPetNicknameButtonVisible() {
        // given
        sharedPrefs.edit().putLong(context.getString(R.string.saved_user_points_key), 1000L).apply()
        launchFragmentInContainer<HomeFragment>(Bundle(), R.style.DefaultAppTheme)

        // when
        onView(withId(R.id.pet_nickname_text)).check(matches(isDisplayed())).perform(click())

        // then
        onView(withId(R.id.pet_nickname_edit)).check(matches(isDisplayed()))
        onView(withId(R.id.pet_nickname_edit)).check(matches(withHint("Adj egy becenevet a kiwidnek")))
        onView(withId(R.id.pet_nickname_button)).check(matches(isDisplayed()))
        onView(withId(R.id.pet_nickname_text)).check(matches(not(isDisplayed())))
    }

    @Suppress("UnsafeCallOnNullableType")
    @Test
    fun testClickingOnGoldLayoutShouldNavigateToShopView() {
        // given
        val scenario = launchFragmentInContainer<HomeFragment>(Bundle(), R.style.DefaultAppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // when
        onView(withId(R.id.gold_layout)).perform(click())

        // then
        Mockito.verify(navController)
            .navigate(HomeFragmentDirections.actionNavigationHomeToShopFragment())
    }
}
