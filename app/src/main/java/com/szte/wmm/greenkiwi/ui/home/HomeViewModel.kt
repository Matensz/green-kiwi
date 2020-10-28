package com.szte.wmm.greenkiwi.ui.home

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.szte.wmm.greenkiwi.HungerAlarmReceiver
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.util.cancelNotifications
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt
import kotlin.math.truncate

class HomeViewModel(
    currentPoints: Long,
    private val expBaseNumber: Int,
    private val app: Application
) : AndroidViewModel(app) {

    companion object {
        private const val HUNGER_NOTIFICATION_ID = 0
        //TODO set correct value when done testing
        private const val ONE_DAY_IN_MILLIS = 60000L
        private const val ONE_SECOND_IN_MILLIS = 3000L
    }

    private val _levelUps = MutableLiveData<Int>()
    val levelUps: LiveData<Int>
        get() = _levelUps
    private val _experience = MutableLiveData<ValuePair>()
    val experience: LiveData<ValuePair>
        get() = _experience
    private val _petImage = MutableLiveData<Int>()
    val petImage: LiveData<Int>
        get() = _petImage
    private val _hunger = MutableLiveData<ValuePair>()
    val hunger: LiveData<ValuePair>
        get() = _hunger

    private val hungerTimerKey = app.getString(R.string.hunger_timer_key)
    private val sharedPreferences = app.getSharedPreferences(app.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(app, HungerAlarmReceiver::class.java)
    private val notifyPendingIntent: PendingIntent
    private lateinit var timer: CountDownTimer

    init {
        val levelUps = calculateLevelUpsInExpRange(currentPoints)
        val currentPlayerLevel = levelUps + 1
        val maxExpAtPreviousLevel = calculateMaxExpAtLevel(levelUps)
        val maxExpAtCurrentLevel = calculateMaxExpAtLevel(currentPlayerLevel)

        _levelUps.value = levelUps
        _petImage.value = getPetImageByLevel(currentPlayerLevel)
        _experience.value = ValuePair(currentPoints - maxExpAtPreviousLevel, maxExpAtCurrentLevel - maxExpAtPreviousLevel)

        notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            HUNGER_NOTIFICATION_ID,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (currentPlayerLevel > 4) {
            calculatePetHunger()
        }
    }

    private fun calculateLevelUpsInExpRange(currentExp: Long): Int {
        val levelUpsInExpRange = (sqrt((expBaseNumber * expBaseNumber + 4 * expBaseNumber * currentExp).toDouble()) - expBaseNumber) / (2 * expBaseNumber)
        return truncate(levelUpsInExpRange).toInt()
    }

    private fun calculateMaxExpAtLevel(level: Int) = expBaseNumber.toLong() * level * (1 + level)

    private fun getPetImageByLevel(playerLevel: Int): Int {
        return when(playerLevel) {
            0, 1, 2 -> R.drawable.egg
            3, 4 -> R.drawable.egg_cracked
            else -> R.drawable.kiwi
        }
    }

    private fun calculatePetHunger() {
        val notificationManager = ContextCompat.getSystemService(app, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotifications()

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + ONE_DAY_IN_MILLIS,
            notifyPendingIntent
        )

        viewModelScope.launch {
            saveTime()
        }
        createHungerTimer()
    }

    /**
     * Creates a new timer
     */
    private fun createHungerTimer() {
        viewModelScope.launch {
            val startTime = loadTime()
            val triggerTime = startTime + ONE_DAY_IN_MILLIS
            timer = object : CountDownTimer(triggerTime, ONE_SECOND_IN_MILLIS) {

                override fun onTick(millisUntilFinished: Long) {
                    val remainingTime = triggerTime - System.currentTimeMillis()
                    if (remainingTime > 0) {
                        _hunger.value = ValuePair(remainingTime, ONE_DAY_IN_MILLIS)
                    } else {
                        _hunger.value = ValuePair(1, 100)
                        cancelAlarm()
                    }
                }

                override fun onFinish() {
                    cancelAlarm()
                }
            }
            timer.start()
        }
    }

    private fun cancelAlarm() {
        timer.cancel()
        alarmManager.cancel(notifyPendingIntent)
    }

    private suspend fun saveTime() =
        withContext(Dispatchers.IO) {
            val savedTime = sharedPreferences.getLong(hungerTimerKey, 0)
            if (savedTime == 0L) {
                sharedPreferences.edit().putLong(hungerTimerKey, System.currentTimeMillis()).apply()
            }
        }

    private suspend fun loadTime(): Long =
        withContext(Dispatchers.IO) {
            sharedPreferences.getLong(hungerTimerKey, 0)
        }
}

/**
 * Data class storing the current value and the maximum value for a given feature.
 */
data class ValuePair(var currentValue: Long, var currentMaxValue: Long)
