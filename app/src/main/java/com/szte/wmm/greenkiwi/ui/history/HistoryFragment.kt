package com.szte.wmm.greenkiwi.ui.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.szte.wmm.greenkiwi.GreenKiwiApplication
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentHistoryBinding
import com.szte.wmm.greenkiwi.util.getResIdForImageName
import kotlinx.coroutines.Dispatchers

/**
 * Fragment for the history view.
 */
class HistoryFragment : Fragment() {

    companion object {
        private const val DEFAULT_PET_IMAGE_NAME = "kiwi_green"
    }

    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((requireContext().applicationContext as GreenKiwiApplication).userSelectedActivitiesRepository, Dispatchers.IO)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application
        val sharedPref = application.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val binding: FragmentHistoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        binding.lifecycleOwner = this

        val adapter = ActivityHistoryAdapter(requireContext())
        binding.activityHistoryList.adapter = adapter
        historyViewModel.activityHistoryList.observe(viewLifecycleOwner, {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })

        historyViewModel.dailyActivityCount.observe(viewLifecycleOwner, {
            binding.dailyActivityNotification.text = getFormattedCounter(it)
        })

        historyViewModel.kiwiImageKey.observe(viewLifecycleOwner, {
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
