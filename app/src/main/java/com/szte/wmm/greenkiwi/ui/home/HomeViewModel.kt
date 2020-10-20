package com.szte.wmm.greenkiwi.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.szte.wmm.greenkiwi.R
import kotlin.math.sqrt
import kotlin.math.truncate

class HomeViewModel(currentPoints: Long, private val expBaseNumber: Int) : ViewModel() {

    private val _levelUps = MutableLiveData<Int>()
    val levelUps: LiveData<Int>
        get() = _levelUps
    private val _experience = MutableLiveData<Experience>()
    val experience: LiveData<Experience>
        get() = _experience
    private val _petImage = MutableLiveData<Int>()
    val petImage: LiveData<Int>
        get() = _petImage

    init {
        val levelUps = calculateLevelUpsInExpRange(currentPoints)
        val currentPlayerLevel = levelUps + 1
        val maxExpAtPreviousLevel = calculateMaxExpAtLevel(levelUps)
        val maxExpAtCurrentLevel = calculateMaxExpAtLevel(currentPlayerLevel)

        _levelUps.value = levelUps
        _petImage.value = getPetImageByLevel(currentPlayerLevel)
        _experience.value = Experience(currentPoints - maxExpAtPreviousLevel, maxExpAtCurrentLevel - maxExpAtPreviousLevel)
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
}

/**
 * Data class storing the current exp and the maximum exp, both relative to the current player level.
 */
data class Experience(var currentExp: Long, var currentMaxExp: Long)
