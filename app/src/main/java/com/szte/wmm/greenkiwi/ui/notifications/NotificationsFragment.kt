package com.szte.wmm.greenkiwi.ui.notifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentNotificationsBinding
import com.szte.wmm.greenkiwi.util.InjectorUtils
import com.szte.wmm.greenkiwi.util.getResIdForImageName

class NotificationsFragment : Fragment() {

    companion object {
        private const val DEFAULT_PET_IMAGE_NAME = "kiwi_green"
    }

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application
        val sharedPref = application.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val viewModelFactory = InjectorUtils.getNotificationsViewModelFactory(this)
        notificationsViewModel = ViewModelProvider(this, viewModelFactory).get(NotificationsViewModel::class.java)
        val binding: FragmentNotificationsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)

        binding.lifecycleOwner = this

        val adapter = ActivityHistoryAdapter(requireContext())
        binding.activityHistoryList.adapter = adapter
        notificationsViewModel.activityHistoryList.observe(viewLifecycleOwner, {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })

        notificationsViewModel.dailyActivityCount.observe(viewLifecycleOwner, {
            binding.dailyActivityNotification.text = getFormattedCounter(it)
        })

        notificationsViewModel.kiwiImageKey.observe(viewLifecycleOwner, {
            val petImageName = sharedPref.getString(getString(it), DEFAULT_PET_IMAGE_NAME)
            val petImageResId = petImageName?.let { name -> getResIdForImageName(application, name) } ?: R.drawable.kiwi_green
            binding.petImage.setImageResource(petImageResId)
        })

        return binding.root
    }

    private fun getFormattedCounter(currentCount: Int): String {
        val maxCount = resources.getInteger(R.integer.daily_activity_max_count)
        val goldReward = resources.getInteger(R.integer.daily_counter_gold_reward)
        return if (currentCount < maxCount) {
            String.format(getString(R.string.daily_activity_count_info_incomplete), currentCount, maxCount, maxCount - currentCount, goldReward)
        } else {
            String.format(getString(R.string.daily_activity_count_info_completed), maxCount)
        }
    }
}