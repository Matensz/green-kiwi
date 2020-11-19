package com.szte.wmm.greenkiwi.ui.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.szte.wmm.greenkiwi.CoroutineTestRule
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.ShopRepository
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

/**
 * Unit test for SettingsViewModel.
 * @see SettingsViewModel
 */
@ExperimentalCoroutinesApi
@Config(sdk = [28])
@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest {

    companion object {
        private const val DEFAULT_BACKGROUND_RESOURCE_NAME = "default_wallpaper_name"
        private const val DEFAULT_PET_IMAGE_RESOURCE_NAME = "green_kiwi_name"
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var userSelectedActivitiesRepository: UserSelectedActivitiesRepository

    @Mock
    private lateinit var shopRepository: ShopRepository

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var application: Application
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var underTest: SettingsViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        application = ApplicationProvider.getApplicationContext()
        sharedPreferences = application.getSharedPreferences(application.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        underTest = SettingsViewModel(userSelectedActivitiesRepository, shopRepository, application, testDispatcher)
    }

    @Test
    fun `test resetUserValues() should reset repository data properly`() = coroutineTestRule.runBlockingTest {
        // given

        // when
        underTest.resetUserValues()

        // then
        verifyBlocking(userSelectedActivitiesRepository, times(1)){ deleteAllAddedActivities() }
        verifyBlocking(shopRepository, times(1)){ resetPurchaseStatuses(DEFAULT_BACKGROUND_RESOURCE_NAME, DEFAULT_PET_IMAGE_RESOURCE_NAME) }
        verifyNoMoreInteractions(userSelectedActivitiesRepository)
        verifyNoMoreInteractions(shopRepository)
    }

    @Test
    fun `test saveNightModeSettings() should save settings to shared preferences`() = coroutineTestRule.runBlockingTest {
        // given
        val nightModeSettingKey = application.getString(R.string.night_mode_setting_key)
        sharedPreferences.edit().remove(nightModeSettingKey).apply()

        // when
        underTest.saveNightModeSettings(true)

        // then
        val nightModeSetting = sharedPreferences.getBoolean(nightModeSettingKey, false)
        assertTrue(nightModeSetting)
    }
}
