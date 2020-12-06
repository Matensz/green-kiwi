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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.szte.wmm.greenkiwi.HungerAlarmReceiver
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import com.szte.wmm.greenkiwi.util.cancelNotifications
import com.szte.wmm.greenkiwi.util.getResIdForImageName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.sqrt
import kotlin.math.truncate

/**
 * View model for the home view.
 */
@Suppress("TooManyFunctions")
class HomeViewModel(
    private val activitiesRepository: ActivitiesRepository,
    private val app: Application,
    private val defaultDispatcher: CoroutineDispatcher
) : AndroidViewModel(app) {

    companion object {
        private const val DEFAULT_PET_IMAGE_NAME = "kiwi_green"
        private const val HUNGER_NOTIFICATION_ID = 0
        private const val EGG_HATCHED_LEVEL = 5
        private const val HUNGER_BAR_MIN_VALUE = 1L
        private const val HUNGER_BAR_MAX_VALUE = 100L
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
    private val _navigateToShop = MutableLiveData<Boolean?>()
    val navigateToShop: LiveData<Boolean?>
        get() = _navigateToShop

    private val expBaseNumber = app.resources.getInteger(R.integer.exp_base_number)
    private val hungerTimerKey = app.getString(R.string.hunger_timer_key)
    private val playerGoldKey = app.getString(R.string.saved_user_gold_key)
    private val sharedPreferences = app.getSharedPreferences(app.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(app, HungerAlarmReceiver::class.java)
    private val notifyPendingIntent: PendingIntent
    private lateinit var timer: CountDownTimer

    init {
        warmUpDatabase()

        val currentPoints = getPoints()
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
            app,
            HUNGER_NOTIFICATION_ID,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (currentPlayerLevel >= EGG_HATCHED_LEVEL) {
            calculatePetHunger()
        }
    }

    /**
     * This view does not use the result of this query, it's needed for database initialization to fasten up the first real query.
     */
    private fun warmUpDatabase() {
        viewModelScope.launch {
            withContext(defaultDispatcher) {
                activitiesRepository.getActivities()
            }
        }
        Timber.i("Warm up call to database fired")
    }

    fun feedPet() {
        cancelAlarm()
        _feedButtonVisible.value = false
        sharedPreferences.edit().remove(hungerTimerKey).apply()
        subtractFoodPriceFromGold()
        calculatePetHunger()
    }

    private fun getPoints(): Long {
        val defaultValue = app.resources.getInteger(R.integer.default_starting_point).toLong()
        return sharedPreferences.getLong(app.getString(R.string.saved_user_points_key), defaultValue)
    }

    @Suppress("MagicNumber") //4 and 2 are the constant numbers of the quadratic formula
    private fun calculateLevelUpsInExpRange(currentExp: Long): Int {
        val levelUpsInExpRange = (sqrt((expBaseNumber * expBaseNumber + 4 * expBaseNumber * currentExp).toDouble()) - expBaseNumber) / (2 * expBaseNumber)
        return truncate(levelUpsInExpRange).toInt()
    }

    private fun calculateMaxExpAtLevel(level: Int) = expBaseNumber.toLong() * level * (1 + level)

    @Suppress("MagicNumber")
    private fun getPetImageByLevel(playerLevel: Int): Int {
        return when(playerLevel) {
            0, 1, 2 -> R.drawable.egg
            3, 4 -> R.drawable.egg_cracked
            else -> getCurrentPetImage()
        }
    }

    private fun getCurrentPetImage(): Int {
        val resName = sharedPreferences.getString(app.getString(R.string.current_pet_image_key), DEFAULT_PET_IMAGE_NAME) ?: DEFAULT_PET_IMAGE_NAME
        return getResIdForImageName(app, resName)
    }

    private fun getCurrentBackground(): Int? {
        val resName = sharedPreferences.getString(app.getString(R.string.current_background_key), "")
        return resName.let { if (!it.isNullOrEmpty()) getResIdForImageName(app, it) else null }
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
     * Creates a new timer for pet hunger.
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
                        _hunger.value = ValuePair(HUNGER_BAR_MIN_VALUE, HUNGER_BAR_MAX_VALUE)
                        _feedButtonVisible.value = true
                        timer.cancel()
                        Timber.d("Pet hunger timer canceled in CountDownTimer.onTick()")
                    }
                }

                override fun onFinish() {
                    timer.cancel()
                    Timber.d("Pet hunger timer canceled in CountDownTimer.onFinish()")
                }
            }
            timer.start()
        }
    }

    private fun cancelAlarm() {
        alarmManager.cancel(notifyPendingIntent)
        Timber.d("Pet hunger notification canceled")
    }

    private suspend fun saveTime() =
        withContext(defaultDispatcher) {
            val savedTime = sharedPreferences.getLong(hungerTimerKey, 0)
            if (savedTime == 0L) {
                sharedPreferences.edit().putLong(hungerTimerKey, System.currentTimeMillis()).apply()
            }
        }

    private suspend fun loadTime(): Long =
        withContext(defaultDispatcher) {
            sharedPreferences.getLong(hungerTimerKey, 0)
        }

    private fun initGoldCounter() {
        viewModelScope.launch {
            _gold.value = getPlayerGold()
        }
    }

    private suspend fun getPlayerGold(): Long =
        withContext(defaultDispatcher) {
            sharedPreferences.getLong(playerGoldKey, 0L)
        }

    private fun subtractFoodPriceFromGold() {
        viewModelScope.launch {
            _gold.value = updatePlayerGold()
        }
    }

    private suspend fun updatePlayerGold(): Long {
        return withContext(defaultDispatcher) {
            val currentGold = sharedPreferences.getLong(playerGoldKey, 0L)
            val updatedGold = currentGold - app.resources.getInteger(R.integer.food_price_in_gold)
            sharedPreferences.edit().putLong(playerGoldKey, updatedGold).apply()
            updatedGold
        }
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
