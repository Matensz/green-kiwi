package com.szte.wmm.greenkiwi.ui.shop

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.chip.Chip
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.TestShopRepository
import com.szte.wmm.greenkiwi.repository.domain.ShopCategory
import com.szte.wmm.greenkiwi.repository.domain.ShopItem
import com.szte.wmm.greenkiwi.util.ServiceLocator
import com.szte.wmm.greenkiwi.util.atPosition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test for testing ShopFragment.
 * @see ShopFragment
 */
@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ShopFragmentTest {

    private lateinit var testRepository: TestShopRepository
    private lateinit var context: Context
    private lateinit var sharedPrefs: SharedPreferences

    @Before
    fun initRepository() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        sharedPrefs.edit().remove(context.getString(R.string.saved_user_gold_key)).apply()
        testRepository = TestShopRepository()
        testRepository.addShopItems(listOf(
            ShopItem(1L, "default_wallpaper_name", "", 1, ShopCategory.BACKGROUND, true),
            ShopItem(2L, "kiwi_wallpaper_name", "background_kiwi", 1, ShopCategory.BACKGROUND, false),
            ShopItem(3L, "green_kiwi_name", "kiwi_green", 1, ShopCategory.PET_IMAGE, true),
            ShopItem(4L, "brown_kiwi_name", "kiwi_brown", 1, ShopCategory.PET_IMAGE, false)
        ))
        ServiceLocator.shopRepository = testRepository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepositories()
        sharedPrefs.edit().remove(context.getString(R.string.saved_user_gold_key)).apply()
    }

    @Test
    fun testShopItemListIsDisplayed() {
        // given

        // when
        launchFragmentInContainer<ShopFragment>(Bundle(), R.style.DefaultAppTheme)

        // then
        onView(withId(R.id.shop_categories)).check(matches(isDisplayed()))
        onView(withId(R.id.shop_list)).check(matches(isDisplayed()))
        onView(withId(R.id.shop_list)).check(matches(atPosition(0, hasDescendant(withText(context.getString(R.string.default_wallpaper_name))))))
        onView(withId(R.id.shop_list)).check(matches(atPosition(1, hasDescendant(withText(context.getString(R.string.kiwi_wallpaper_name))))))
        onView(withId(R.id.shop_list)).check(matches(atPosition(2, hasDescendant(withText(context.getString(R.string.green_kiwi_name))))))
        onView(withId(R.id.shop_list)).check(matches(atPosition(3, hasDescendant(withText(context.getString(R.string.brown_kiwi_name))))))
    }

    @Test
    fun testShopCategoryChipShouldFilterShopItemList() {
        // given
        launchFragmentInContainer<ShopFragment>(Bundle(), R.style.DefaultAppTheme)

        // when
        onView(allOf(`is`(Matchers.instanceOf(Chip::class.java)), withText("Háttér"))).perform(ViewActions.click())


        // then
        onView(withId(R.id.shop_categories)).check(matches(isDisplayed()))
        onView(withId(R.id.shop_list)).check(matches(isDisplayed()))
        onView(withId(R.id.shop_list)).check(matches(atPosition(0, hasDescendant(withText(context.getString(R.string.default_wallpaper_name))))))
        onView(withId(R.id.shop_list)).check(matches(atPosition(1, hasDescendant(withText(context.getString(R.string.kiwi_wallpaper_name))))))
        onView(withId(R.id.shop_list)).check(matches(ViewMatchers.hasChildCount(2)))
    }

    @Test
    fun testClickingOnItemShouldDisplayUseItemDialogWithCorrectTextWhenItemIsInUse() {
        // given
        launchFragmentInContainer<ShopFragment>(Bundle(), R.style.DefaultAppTheme)

        // when
        onView(withId(R.id.shop_list)).perform(RecyclerViewActions.actionOnItemAtPosition<ShopItemGridAdapter.ShopGridItemHolder>(0, ViewActions.click()))

        // then
        onView(withId(R.id.alertTitle)).inRoot(isDialog())
            .check(matches(withText(R.string.default_wallpaper_name))).check(matches(isDisplayed()))
        onView(ViewMatchers.withResourceName("message")).inRoot(isDialog())
            .check(matches(withText("Már ez a háttér az aktuálisan beállított."))).check(matches(isDisplayed()))
        onView(withId(android.R.id.button2)).inRoot(isDialog())
            .check(matches(withText(R.string.shop_item_dialog_negative_button_text))).check(matches(isDisplayed()))
    }

    @Test
    fun testClickingOnItemShouldDisplayUseItemDialogWithCorrectTextWhenItemIsNotInUseButIsPurchased() = runBlockingTest {
        // given
        testRepository.updateShopItemById(2L, true)
        launchFragmentInContainer<ShopFragment>(Bundle(), R.style.DefaultAppTheme)

        // when
        onView(withId(R.id.shop_list)).perform(RecyclerViewActions.actionOnItemAtPosition<ShopItemGridAdapter.ShopGridItemHolder>(1, ViewActions.click()))

        // then
        onView(withId(R.id.alertTitle)).inRoot(isDialog())
            .check(matches(withText(R.string.kiwi_wallpaper_name))).check(matches(isDisplayed()))
        onView(ViewMatchers.withResourceName("message")).inRoot(isDialog())
            .check(matches(withText("Kiválasztott háttér beállítása az aktuális helyett?"))).check(matches(isDisplayed()))
        onView(withId(android.R.id.button2)).inRoot(isDialog())
            .check(matches(withText(R.string.shop_item_dialog_negative_button_text))).check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).inRoot(isDialog())
            .check(matches(withText(R.string.shop_item_dialog_positive_button_text_use))).check(matches(isDisplayed()))
    }

    @Test
    fun testClickingOnItemShouldDisplayPurchaseItemDialogWithCorrectTextWhenItemIsNotPurchasedAndPlayerHasEnoughGold() {
        // given
        sharedPrefs.edit().putLong(context.getString(R.string.saved_user_gold_key), 100L).apply()
        launchFragmentInContainer<ShopFragment>(Bundle(), R.style.DefaultAppTheme)

        // when
        onView(withId(R.id.shop_list)).perform(RecyclerViewActions.actionOnItemAtPosition<ShopItemGridAdapter.ShopGridItemHolder>(1, ViewActions.click()))

        // then
        onView(withId(R.id.alertTitle)).inRoot(isDialog())
            .check(matches(withText(R.string.kiwi_wallpaper_name))).check(matches(isDisplayed()))
        onView(ViewMatchers.withResourceName("message")).inRoot(isDialog())
            .check(matches(withText("Ez a háttér megvásárolható 1 aranyért. Szeretnéd megvásárolni?"))).check(matches(isDisplayed()))
        onView(withId(android.R.id.button2)).inRoot(isDialog())
            .check(matches(withText(R.string.shop_item_dialog_negative_button_text))).check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).inRoot(isDialog())
            .check(matches(withText(R.string.shop_item_dialog_positive_button_text_buy))).check(matches(isDisplayed()))
    }

    @Test
    fun testClickingOnItemShouldDisplayPurchaseItemDialogWithCorrectTextWhenItemIsNotPurchasedAndPlayerHasNotEnoughGold() {
        // given
        launchFragmentInContainer<ShopFragment>(Bundle(), R.style.DefaultAppTheme)

        // when
        onView(withId(R.id.shop_list)).perform(RecyclerViewActions.actionOnItemAtPosition<ShopItemGridAdapter.ShopGridItemHolder>(1, ViewActions.click()))

        // then
        onView(withId(R.id.alertTitle)).inRoot(isDialog())
            .check(matches(withText(R.string.kiwi_wallpaper_name))).check(matches(isDisplayed()))
        onView(ViewMatchers.withResourceName("message")).inRoot(isDialog())
            .check(matches(
                withText("Sajnos nincs elég aranyad (1) ahhoz, hogy ez a háttér a tiéd legyen. Előtte nézz szét a tevékenységek között, és gyűjts még egy kis aranyat!")))
            .check(matches(isDisplayed()))
        onView(withId(android.R.id.button2)).inRoot(isDialog())
            .check(matches(withText(R.string.shop_item_dialog_negative_button_text))).check(matches(isDisplayed()))
    }
}
