package com.szte.wmm.greenkiwi.repository

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.szte.wmm.greenkiwi.data.local.ShopDao
import com.szte.wmm.greenkiwi.repository.domain.ShopCategory
import com.szte.wmm.greenkiwi.repository.domain.ShopItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit tests for ShopRepository.
 * @see ShopRepository
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ShopRepositoryTest {

    @Mock
    private lateinit var shopDao: ShopDao

    @Test
    fun `test getShopItems() should delegateto dao method`() = runBlockingTest {
        // given
        shopDao.stub {
            onBlocking { getShopItems() }.doReturn(listOf(createDatabaseShopItem(1L), createDatabaseShopItem(2L)))
        }
        val expectedResult = listOf(createDomainShopItem(1L), createDomainShopItem(2L))
        val underTest = ShopRepository(shopDao)

        // when
        val actual = underTest.getShopItems()

        // then
        assertThat(actual.size, `is`(equalTo(2)))
        assertThat(actual, `is`(equalTo(expectedResult)))
        verifyBlocking(shopDao, times(1), ShopDao::getShopItems)
        verifyNoMoreInteractions(shopDao)
    }

    @Test
    fun `test updateShopItems() should delegate to dao method`() = runBlockingTest {
        // given
        shopDao.stub {
            onBlocking { updateShopItemById(1L, true) }.doReturn(1)
        }
        val underTest = ShopRepository(shopDao)

        // when
        val actual = underTest.updateShopItemById(1L, true)

        // then
        assertThat(actual, `is`(equalTo(1)))
        verifyBlocking(shopDao, times(1)) { updateShopItemById(1L, true) }
        verifyNoMoreInteractions(shopDao)
    }

    @Test
    fun `test resetPurchaseStatuses() should delegate to dao method`() = runBlockingTest {
        // given
        val underTest = ShopRepository(shopDao)

        // when
        underTest.resetPurchaseStatuses("titleRes", "titleRes")

        // then
        verifyBlocking(shopDao, times(1)){ resetPurchaseStatuses("titleRes", "titleRes") }
        verifyNoMoreInteractions(shopDao)
    }

    private fun createDatabaseShopItem(id: Long): com.szte.wmm.greenkiwi.data.local.model.ShopItem {
        return com.szte.wmm.greenkiwi.data.local.model.ShopItem(id, "titleRes", "imageRes", 1, 1, false)
    }

    private fun createDomainShopItem(id: Long): ShopItem {
        return ShopItem(id, "titleRes", "imageRes", 1, ShopCategory.BACKGROUND, false)
    }
}
