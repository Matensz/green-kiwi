package com.szte.wmm.greenkiwi.ui.shop

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.szte.wmm.greenkiwi.CoroutineTestRule
import com.szte.wmm.greenkiwi.createShopItem
import com.szte.wmm.greenkiwi.repository.ShopRepository
import com.szte.wmm.greenkiwi.repository.domain.ShopCategory
import com.szte.wmm.greenkiwi.repository.domain.ShopItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

/**
 * Unit test for ShopViewModel.
 * @see ShopViewModel
 */
@ExperimentalCoroutinesApi
@Config(sdk = [28])
@RunWith(AndroidJUnit4::class)
class ShopViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var shopRepository: ShopRepository

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var underTest: ShopViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        shopRepository.stub {
            onBlocking { getShopItems() }.doReturn(listOf(createShopItem(1L, ShopCategory.BACKGROUND, true), createShopItem(2L, ShopCategory.PET_IMAGE, false)))
        }
        shopRepository.stub {
            onBlocking { updateShopItemById(2L, true) }.doReturn(1)
        }
        underTest = ShopViewModel(shopRepository, ApplicationProvider.getApplicationContext(), testDispatcher)
    }

    @Test
    fun `test onFilterChanged() should update filtered items property`() = coroutineTestRule.runBlockingTest {
        // given
        // empty observer for testing
        val observer = Observer<List<ShopItem>> {}
        try {

            // observe the live data
            underTest.filteredItems.observeForever(observer)

            // when
            underTest.onFilterChanged(ShopCategory.PET_IMAGE.name, true)

            // then
            val actualValue = underTest.filteredItems.value
            assertThat(actualValue?.size, `is`(equalTo(1)))
            assertThat(actualValue?.get(0)?.itemId, `is`(equalTo(2L)))
            verifyBlocking(shopRepository, times(1)) { getShopItems() }
            verifyNoMoreInteractions(shopRepository)
        } finally {
            // cleaning up observer
            underTest.filteredItems.removeObserver(observer)
        }
    }

    @Test
    fun `test buySelectedItem() should call repository and refresh items list properly`() = coroutineTestRule.runBlockingTest {
        // given
        // empty observer for testing
        val observer = Observer<List<ShopItem>> {}
        try {

            // observe the live data
            underTest.filteredItems.observeForever(observer)

            // when
            underTest.buySelectedItem(createShopItem(2L, ShopCategory.PET_IMAGE, true))

            // then
            val actualValue = underTest.filteredItems.value
            assertThat(actualValue?.size, `is`(equalTo(2)))
            assertThat(actualValue?.get(0)?.itemId, `is`(equalTo(1L)))
            assertThat(actualValue?.get(1)?.itemId, `is`(equalTo(2L)))
            verifyBlocking(shopRepository, times(2)) { getShopItems() }
            verifyBlocking(shopRepository, times(1)) { updateShopItemById(2L, true) }
            verifyNoMoreInteractions(shopRepository)
        } finally {
            // cleaning up observer
            underTest.filteredItems.removeObserver(observer)
        }
    }
}
