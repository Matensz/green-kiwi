package com.szte.wmm.greenkiwi.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

fun Long.isDayBeforeDate(millis: Long): Boolean {
    val cal1: Calendar = Calendar.getInstance()
    val cal2: Calendar = Calendar.getInstance()
    cal1.timeInMillis = this
    cal2.timeInMillis = millis
    return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
}

fun Long.isSameDay(millis: Long): Boolean {
    val cal1: Calendar = Calendar.getInstance()
    val cal2: Calendar = Calendar.getInstance()
    cal1.timeInMillis = this
    cal2.timeInMillis = millis
    return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
}

@SuppressLint("SimpleDateFormat")
fun formatNullableDateString(lastAddedTimeStamp: Long?, defaultValue: String): String {
    return lastAddedTimeStamp?.let { timeStamp -> SimpleDateFormat("yyyy.MM.dd").format(timeStamp).toString()} ?: defaultValue
}

@SuppressLint("SimpleDateFormat")
fun formatDateString(lastAddedTimeStamp: Long): String {
    return lastAddedTimeStamp.let { timeStamp -> SimpleDateFormat("yyyy.MM.dd").format(timeStamp).toString()}
}
