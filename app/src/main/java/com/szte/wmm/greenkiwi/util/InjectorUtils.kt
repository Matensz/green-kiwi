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
import com.szte.wmm.greenkiwi.ui.history.HistoryViewModelFactory
import com.szte.wmm.greenkiwi.ui.settings.SettingsViewModelFactory
import com.szte.wmm.greenkiwi.ui.shop.ShopViewModelFactory
import kotlinx.coroutines.Dispatchers

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

    fun getHomeViewModelFactory(context: HomeDataContext, fragment: Fragment): HomeViewModelFactory {
        return HomeViewModelFactory(context, getActivitiesRepository(fragment.requireContext()), fragment.requireActivity().application)
    }

    fun getActivitiesViewModelFactory(fragment: Fragment): ActivitiesViewModelFactory {
        return ActivitiesViewModelFactory(getActivitiesRepository(fragment.requireContext()), Dispatchers.IO)
    }

    fun getActivityDetailViewModelFactory(activity: Activity, fragment: Fragment): ActivityDetailViewModelFactory {
        return ActivityDetailViewModelFactory(activity, getUserSelectedActivitiesRepository(fragment.requireContext()), Dispatchers.IO)
    }

    fun getHistoryViewModelFactory(fragment: Fragment): HistoryViewModelFactory {
        return HistoryViewModelFactory(getUserSelectedActivitiesRepository(fragment.requireContext()), Dispatchers.IO)
    }

    fun getSettingsViewModelFactory(fragment: Fragment, application: Application): SettingsViewModelFactory {
        return SettingsViewModelFactory(getUserSelectedActivitiesRepository(fragment.requireContext()), getShopRepository(fragment.requireContext()), application, Dispatchers.IO)
    }

    fun getShopViewModelFactory(fragment: Fragment, application: Application): ShopViewModelFactory {
        return ShopViewModelFactory(getShopRepository(fragment.requireContext()), application, Dispatchers.IO)
    }
}
