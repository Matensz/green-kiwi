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
import com.szte.wmm.greenkiwi.ui.home.context.HomeDataContext
import com.szte.wmm.greenkiwi.util.cancelNotifications
import kotlinx.coroutines.*
import kotlin.math.sqrt
import kotlin.math.truncate

class HomeViewModel(context: HomeDataContext, private val app: Application) : AndroidViewModel(app) {

    companion object {
        private const val DEFAULT_PET_IMAGE_NAME = "kiwi_green"
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
    private val _currentBackground = MutableLiveData<Int?>()
    val currentBackground: LiveData<Int?>
        get() = _currentBackground
    private val _hunger = MutableLiveData<ValuePair>()
    val hunger: LiveData<ValuePair>
        get() = _hunger
    private val _gold = MutableLiveData<Long>()
    val gold: LiveData<Long>
        get() = _gold
    private val _feedButtonVisible = MutableLiveData<Boolean>()
    val feedButtonVisible: LiveData<Boolean>
        get() = _feedButtonVisible
    private val _navigateToShop = MutableLiveData<Boolean>()
    val navigateToShop: LiveData<Boolean>
        get() = _navigateToShop

    private val currentPoints = context.currentPoints
    private val expBaseNumber = context.expBaseNumber
    private val hungerTimerKey = app.getString(R.string.hunger_timer_key)
    private val playerGoldKey = app.getString(R.string.saved_user_gold_key)
    private val sharedPreferences = app.getSharedPreferences(app.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(app, HungerAlarmReceiver::class.java)
    private val notifyPendingIntent: PendingIntent
    private lateinit var timer: CountDownTimer

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        val levelUps = calculateLevelUpsInExpRange(currentPoints)
        val currentPlayerLevel = levelUps + 1
        val maxExpAtPreviousLevel = calculateMaxExpAtLevel(levelUps)
        val maxExpAtCurrentLevel = calculateMaxExpAtLevel(currentPlayerLevel)

        _levelUps.value = levelUps
        _petImage.value = getPetImageByLevel(currentPlayerLevel)
        _currentBackground.value = getCurrentBackground()
        _experience.value = ValuePair(currentPoints - maxExpAtPreviousLevel, maxExpAtCurrentLevel - maxExpAtPreviousLevel)
        initGoldCounter()

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

    fun feedPet() {
        cancelAlarm()
        _feedButtonVisible.value = false
        sharedPreferences.edit().remove(hungerTimerKey).apply()
        subtractFoodPriceFromGold()
        calculatePetHunger()
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
            else -> getCurrentPetImage()
        }
    }

    private fun getCurrentPetImage(): Int {
        val resName = sharedPreferences.getString(app.getString(R.string.current_pet_image_key), DEFAULT_PET_IMAGE_NAME) ?: DEFAULT_PET_IMAGE_NAME
        return getResIdForImageName(resName)
    }

    private fun getCurrentBackground(): Int? {
        val resName = sharedPreferences.getString(app.getString(R.string.current_background_key), "")
        return resName.let { if (!it.isNullOrEmpty()) getResIdForImageName(it) else null }
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
                        cancelAlarm()
                        _hunger.value = ValuePair(1, 100)
                        _feedButtonVisible.value = true
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

    private fun cancelAlarm() {
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

    private fun initGoldCounter() {
        uiScope.launch {
            _gold.value = getPlayerGold()
        }
    }

    private suspend fun getPlayerGold(): Long =
        withContext(Dispatchers.IO) {
            sharedPreferences.getLong(playerGoldKey, 0L)
        }

    private fun subtractFoodPriceFromGold() {
        uiScope.launch {
            _gold.value = updatePlayerGold()
        }
    }

    private suspend fun updatePlayerGold(): Long {
        return withContext(Dispatchers.IO) {
            val currentGold = sharedPreferences.getLong(playerGoldKey, 0L)
            val updatedGold = currentGold - app.resources.getInteger(R.integer.food_price_in_gold)
            sharedPreferences.edit().putLong(playerGoldKey, updatedGold).apply()
            updatedGold
        }
    }

    private fun getResIdForImageName(resourceName: String): Int {
        return app.resources.getIdentifier(resourceName, "drawable", app.applicationContext.packageName)
    }

    fun navigateToShop() {
        _navigateToShop.value = true
    }

    fun navigateToShopComplete() {
        _navigateToShop.value = null
    }
}

/**
 * Data class storing the current value and the maximum value for a given feature.
 */
data class ValuePair(var currentValue: Long, var currentMaxValue: Long)
