package com.szte.wmm.greenkiwi.ui.activitydetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.repository.domain.Activity

class ActivityDetailViewModelFactory(
        private val activity: Activity,
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ActivityDetailViewModel::class.java)) {
                return ActivityDetailViewModel(activity, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
