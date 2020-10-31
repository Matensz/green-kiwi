package com.szte.wmm.greenkiwi.util

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.szte.wmm.greenkiwi.data.local.ApplicationDatabase
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import com.szte.wmm.greenkiwi.repository.UserSelectedActivitiesRepository
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.ui.activities.ActivitiesViewModelFactory
import com.szte.wmm.greenkiwi.ui.activitydetail.ActivityDetailViewModelFactory
import com.szte.wmm.greenkiwi.ui.home.HomeViewModelFactory
import com.szte.wmm.greenkiwi.ui.home.context.HomeDataContext
import com.szte.wmm.greenkiwi.ui.settings.SettingsViewModelFactory

/**
 * Static utility methods for dependency injection.
 */
object InjectorUtils {

    private fun getActivitiesRepository(context: Context): ActivitiesRepository {
        return ActivitiesRepository.getInstance(ApplicationDatabase.getInstance(context.applicationContext).activitiesDao())
    }

    private fun getUserSelectedActivitiesRepository(context: Context): UserSelectedActivitiesRepository {
        return UserSelectedActivitiesRepository.getInstance(ApplicationDatabase.getInstance(context.applicationContext).userSelectedActivitiesDao())
    }

    fun getHomeViewModelFactory(context: HomeDataContext, fragment: Fragment, application: Application): HomeViewModelFactory {
        return HomeViewModelFactory(context, getUserSelectedActivitiesRepository(fragment.requireContext()), application)
    }

    fun getActivitiesViewModelFactory(fragment: Fragment): ActivitiesViewModelFactory {
        return ActivitiesViewModelFactory(getActivitiesRepository(fragment.requireContext()))
    }

    fun getActivityDetailViewModelFactory(activity: Activity, fragment: Fragment, application: Application): ActivityDetailViewModelFactory {
        return ActivityDetailViewModelFactory(activity, getUserSelectedActivitiesRepository(fragment.requireContext()), application)
    }

    fun getSettingsViewModelFactory(fragment: Fragment, application: Application): SettingsViewModelFactory {
        return SettingsViewModelFactory(getUserSelectedActivitiesRepository(fragment.requireContext()), application)
    }
}
