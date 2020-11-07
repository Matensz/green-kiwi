package com.szte.wmm.greenkiwi.util

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.szte.wmm.greenkiwi.data.local.ApplicationDatabase
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import com.szte.wmm.greenkiwi.repository.ShopRepository
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.ui.activities.ActivitiesViewModelFactory
import com.szte.wmm.greenkiwi.ui.activitydetail.ActivityDetailViewModelFactory
import com.szte.wmm.greenkiwi.ui.home.HomeViewModelFactory
import com.szte.wmm.greenkiwi.ui.home.context.HomeDataContext
import com.szte.wmm.greenkiwi.ui.notifications.NotificationsViewModelFactory
import com.szte.wmm.greenkiwi.ui.settings.SettingsViewModelFactory
import com.szte.wmm.greenkiwi.ui.shop.ShopViewModelFactory

/**
 * Static utility methods for dependency injection.
 */
object InjectorUtils {

    private fun getDatabase(context: Context): ApplicationDatabase {
        return ApplicationDatabase.getInstance(context.applicationContext)
    }

    private fun getActivitiesRepository(context: Context): ActivitiesRepository {
        return ActivitiesRepository.getInstance(getDatabase(context).activitiesDao())
    }

    private fun getUserSelectedActivitiesRepository(context: Context): UserSelectedActivitiesRepository {
        return UserSelectedActivitiesRepository.getInstance(getDatabase(context).userSelectedActivitiesDao())
    }

    private fun getShopRepository(context: Context): ShopRepository {
        return ShopRepository.getInstance(getDatabase(context).shopDao())
    }

    fun getHomeViewModelFactory(context: HomeDataContext, application: Application): HomeViewModelFactory {
        return HomeViewModelFactory(context, application)
    }

    fun getActivitiesViewModelFactory(fragment: Fragment): ActivitiesViewModelFactory {
        return ActivitiesViewModelFactory(getActivitiesRepository(fragment.requireContext()))
    }

    fun getActivityDetailViewModelFactory(activity: Activity, fragment: Fragment, application: Application): ActivityDetailViewModelFactory {
        return ActivityDetailViewModelFactory(activity, getUserSelectedActivitiesRepository(fragment.requireContext()), application)
    }

    fun getNotificationsViewModelFactory(fragment: Fragment): NotificationsViewModelFactory {
        return NotificationsViewModelFactory(getUserSelectedActivitiesRepository(fragment.requireContext()))
    }

    fun getSettingsViewModelFactory(fragment: Fragment, application: Application): SettingsViewModelFactory {
        return SettingsViewModelFactory(getUserSelectedActivitiesRepository(fragment.requireContext()), application)
    }

    fun getShopViewModelFactory(fragment: Fragment, application: Application): ShopViewModelFactory {
        return ShopViewModelFactory(getShopRepository(fragment.requireContext()), application)
    }
}
