package com.szte.wmm.greenkiwi

import android.content.Context
import androidx.fragment.app.Fragment
import com.szte.wmm.greenkiwi.data.local.ActivitiesDatabase
import com.szte.wmm.greenkiwi.repository.ActivitiesRepository
import com.szte.wmm.greenkiwi.ui.activities.ActivitiesViewModelFactory

/**
 * Static utility methods for dependency injection.
 */
object InjectorUtils {

    private fun getActivitiesRepository(context: Context): ActivitiesRepository {
        return ActivitiesRepository.getInstance(ActivitiesDatabase.getInstance(context.applicationContext).activitiesDao())
    }

    fun getActivitiesViewModelFactory(fragment: Fragment): ActivitiesViewModelFactory {
        return ActivitiesViewModelFactory(getActivitiesRepository(fragment.requireContext()))
    }
}