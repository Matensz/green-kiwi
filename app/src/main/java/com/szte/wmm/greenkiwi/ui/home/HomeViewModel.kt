package com.szte.wmm.greenkiwi.ui.home

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.*
import com.szte.wmm.greenkiwi.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt
import kotlin.math.truncate

class HomeViewModel(
    currentPoints: Long,
    private val expBaseNumber: Int,
    app: Application
) : AndroidViewModel(app) {

    companion object {
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
    private lateinit var timer: CountDownTimer

    init {
        val levelUps = calculateLevelUpsInExpRange(currentPoints)
        val currentPlayerLevel = levelUps + 1
        val maxExpAtPreviousLevel = calculateMaxExpAtLevel(levelUps)
        val maxExpAtCurrentLevel = calculateMaxExpAtLevel(currentPlayerLevel)

        _levelUps.value = levelUps
        _petImage.value = getPetImageByLevel(currentPlayerLevel)
        _experience.value = ValuePair(currentPoints - maxExpAtPreviousLevel, maxExpAtCurrentLevel - maxExpAtPreviousLevel)
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
                        timer.cancel()
                    }
                }

                override fun onFinish() {
                    timer.cancel()
                }
            }
            timer.start()
        }
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
