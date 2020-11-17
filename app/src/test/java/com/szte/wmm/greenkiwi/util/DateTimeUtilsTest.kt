package com.szte.wmm.greenkiwi.util

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Unit tests for DateTimeUtils.
 */
@RunWith(Parameterized::class)
class DateTimeUtilsTest(
    private val actualTime: Long,
    private val comparedTime: Long,
    private val expectedBeforeResult: Boolean,
    private val expectedSameDayResult: Boolean
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(1605376800000L, 1605463200000L, true, false), //2020-11-14 19:00:00 and 2020-11-15 19:00:00
                arrayOf(1605463200000L, 1605376800000L, false, false), //2020-11-15 19:00:00 and 2020-11-15 19:00:00
                arrayOf(1605376800000L, 1605376800000L, false, true)  //both 2020-11-14 19:00:00
            )
        }
    }

    @Test
    fun `test isDayBeforeDate() should return correct value for given millis`() {
        // given

        // when
        val actual = actualTime.isDayBeforeDate(comparedTime)

        // then
        assertThat(actual, `is`(expectedBeforeResult))
    }

    @Test
    fun `test isSameDay() should return correct value for given millis`() {
        // given

        // when
        val actual = actualTime.isSameDay(comparedTime)

        // then
        assertThat(actual, `is`(expectedSameDayResult))
    }

    @Test
    fun `test formatNullableDateString() should return formatted date for timestamp`() {
        // given
        val givenTimeStamp = 1605376800000L
        val expectedTime = "2020.11.14"

        // when
        val actual = formatNullableDateString(givenTimeStamp, "default")

        // then
        assertThat(actual, equalTo(expectedTime))
    }

    @Test
    fun `test formatNullableDateString() should return formatted date for null value`() {
        // given
        val givenTimeStamp = null
        val expectedTime = "default"

        // when
        val actual = formatNullableDateString(givenTimeStamp, "default")

        // then
        assertThat(actual, equalTo(expectedTime))
    }

    @Test
    fun `test formatDateString() should return formatted date for timestamp`() {
        // given
        val givenTimeStamp = 1605376800000L
        val expectedTime = "2020.11.14"

        // when
        val actual = formatNullableDateString(givenTimeStamp, "default")

        // then
        assertThat(actual, equalTo(expectedTime))
    }
}
