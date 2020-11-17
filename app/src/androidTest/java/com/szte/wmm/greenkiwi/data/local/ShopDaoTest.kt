package com.szte.wmm.greenkiwi.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.szte.wmm.greenkiwi.data.local.model.ShopItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Unit tests for ShopDao.
 * @see ShopDao
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class ShopDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var database: ApplicationDatabase
    private lateinit var shopDao: ShopDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ApplicationDatabase::class.java).allowMainThreadQueries().build()
        shopDao = database.shopDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Throws(Exception::class)
    @Test
    fun testInsertAllAndGetShopItemsShouldWorkCorrectly() = runBlockingTest {
        // given
        val items = listOf(createShopItem(1L, false), createShopItem(2L, false))

        // when
        shopDao.insertAll(items)
        val actual = shopDao.getShopItems()

        // then
        assertThat(actual.size, `is`(equalTo(2)))
        assertThat(actual[0].itemId, `is`(equalTo(1L)))
        assertThat(actual[1].itemId, `is`(equalTo(2L)))
    }

    @Throws(Exception::class)
    @Test
    fun testGetActivityByIdShouldWorkCorrectly() = runBlockingTest {
        // given
        val items = listOf(createShopItem(1L, false), createShopItem(2L, false))
        shopDao.insertAll(items)

        // when
        val updateResult = shopDao.updateShopItemById(2L, true)
        val actualItems = shopDao.getShopItems()

        // then
        assertThat(updateResult, `is`(equalTo(1)))
        assertThat(actualItems.size, `is`(equalTo(2)))
        assertThat(actualItems[0].purchased, `is`(equalTo(false)))
        assertThat(actualItems[1].purchased, `is`(equalTo(true)))
    }

    @Throws(Exception::class)
    @Test
    fun testInsertActivityShouldWorkCorrectly() = runBlockingTest {
        // given
        val items = listOf(createShopItem(1L, true), createShopItem(2L, true), createShopItem(3, true), createShopItem(4, true))
        shopDao.insertAll(items)

        // when
        shopDao.resetPurchaseStatuses("titleName1", "titleName2")
        val actualItems = shopDao.getShopItems()

        // then
        assertThat(actualItems.size, `is`(equalTo(4)))
        assertThat(actualItems[0].purchased, `is`(equalTo(true)))
        assertThat(actualItems[1].purchased, `is`(equalTo(true)))
        assertThat(actualItems[2].purchased, `is`(equalTo(false)))
        assertThat(actualItems[3].purchased, `is`(equalTo(false)))
    }

    private fun createShopItem(id: Long, purchased: Boolean): ShopItem {
        return ShopItem(id, "titleName${id}", "imageName${id}", 1, 1, purchased)
    }
}
