package com.szte.wmm.greenkiwi

import android.app.Application
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import com.szte.wmm.greenkiwi.repository.ShopRepository
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.util.ServiceLocator
import timber.log.Timber

/**
 * GreenKiwi application containing global dependencies with ServiceLocator.
 */
class GreenKiwiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }

    val activitiesRepository: ActivitiesRepository
        get() = ServiceLocator.getActivitiesRepository(this)

    val userSelectedActivitiesRepository: UserSelectedActivitiesRepository
        get() = ServiceLocator.getUserSelectedActivitiesRepository(this)

    val shopRepository: ShopRepository
        get() = ServiceLocator.getShopRepository(this)
}
