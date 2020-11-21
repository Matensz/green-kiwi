package com.szte.wmm.greenkiwi.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withResourceName
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.szte.wmm.greenkiwi.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test for testing SettingsFragment.
 * @see SettingsFragment
 */
@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = getInstrumentation().targetContext
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        sharedPrefs.edit().remove(context.getString(R.string.night_mode_setting_key)).apply()
    }

    @After
    fun cleanUp() {
        sharedPrefs.edit().remove(context.getString(R.string.night_mode_setting_key)).apply()
    }

    @Test
    fun testSettingsAreDisplayedCorrectly() {
        // given

        // when
        launchFragmentInContainer<SettingsFragment>(Bundle(), R.style.DefaultAppTheme)

        // then
        onView(withId(R.id.night_mode_switch)).check(matches(isDisplayed()))
        onView(withId(R.id.night_mode_switch)).check(matches(isNotChecked()))
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()))
        onView(withId(R.id.instructions_button)).check(matches(isDisplayed()))
        onView(withId(R.id.reset_label)).check(matches(isDisplayed()))
        onView(withId(R.id.reset_button)).check(matches(isDisplayed()))
    }

    @Test
    fun testNightModeSettingToggleIsSetUpCorrectly() {
        // given
        sharedPrefs.edit().putBoolean(context.getString(R.string.night_mode_setting_key), true).apply()

        // when
        launchFragmentInContainer<SettingsFragment>(Bundle(), R.style.DefaultAppTheme)

        // then
        onView(withId(R.id.night_mode_switch)).check(matches(isDisplayed()))
        onView(withId(R.id.night_mode_switch)).check(matches(isChecked()))
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()))
        onView(withId(R.id.instructions_button)).check(matches(isDisplayed()))
        onView(withId(R.id.reset_label)).check(matches(isDisplayed()))
        onView(withId(R.id.reset_button)).check(matches(isDisplayed()))
    }

    @Test
    fun testClickingInstructionsButtonShouldOpenInstructionsDialog() {
        // given
        launchFragmentInContainer<SettingsFragment>(Bundle(), R.style.DefaultAppTheme)

        // when
        onView(withId(R.id.instructions_button)).perform(click())

        // then
        onView(withResourceName("icon")).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withId(R.id.alertTitle)).inRoot(isDialog())
            .check(matches(withText(R.string.instructions_title))).check(matches(isDisplayed()))
        onView(withChild(withText(R.string.instructions_first_page))).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).inRoot(isDialog())
            .check(matches(withText(R.string.instructions_dismiss))).check(matches(isDisplayed()))
    }

    @Test
    fun testClickingResetButtonShouldOpenResetDialog() {
        // given
        launchFragmentInContainer<SettingsFragment>(Bundle(), R.style.DefaultAppTheme)

        // when
        onView(withId(R.id.reset_button)).perform(click())

        // then
        onView(withId(R.id.alertTitle)).inRoot(isDialog())
            .check(matches(withText(R.string.reset_dialog_title))).check(matches(isDisplayed()))
        onView(withResourceName("message")).inRoot(isDialog())
            .check(matches(withText(R.string.reset_dialog_text))).check(matches(isDisplayed()))
        onView(withId(android.R.id.button2)).inRoot(isDialog())
            .check(matches(withText(R.string.reset_dialog_negative_button_text))).check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).inRoot(isDialog())
            .check(matches(withText(R.string.reset_dialog_positive_button_text))).check(matches(isDisplayed()))
    }
}
