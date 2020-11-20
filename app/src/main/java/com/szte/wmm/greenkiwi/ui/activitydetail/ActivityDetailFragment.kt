package com.szte.wmm.greenkiwi.ui.activitydetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.szte.wmm.greenkiwi.GreenKiwiApplication
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentActivityDetailBinding
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.util.formatNullableDateString
import com.szte.wmm.greenkiwi.util.isDayBeforeDate
import kotlinx.coroutines.Dispatchers

/**
 * Fragment for the activity details view.
 */
class ActivityDetailFragment : Fragment() {

    private val args: ActivityDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val application = requireNotNull(activity).application

        val viewModelFactory =
            ActivityDetailViewModelFactory(args.selectedActivity, (application as GreenKiwiApplication).userSelectedActivitiesRepository, application, Dispatchers.IO)
        val activityDetailViewModel = ViewModelProvider(this, viewModelFactory).get(ActivityDetailViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentActivityDetailBinding>(inflater, R.layout.fragment_activity_detail, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.activityDetailViewModel = activityDetailViewModel
        binding.callback = object : Callback {

            override fun add(activity: Activity?) {
                activity?.let {
                    val currentTime = System.currentTimeMillis()
                    if (activityDetailViewModel.lastAddedDate.value?.isDayBeforeDate(currentTime) != false) {
                        activityDetailViewModel.addActivity(activity.activityId, currentTime)
                        activityDetailViewModel.updatePlayerValue(activity.point, R.integer.default_starting_point, R.string.saved_user_points_key)
                        val currentDailyCount = activityDetailViewModel.getUpdatedDailyCounter(currentTime)
                        var activityAddedMessage = getString(R.string.activity_added_message)
                        var goldAmount = activity.gold
                        if (currentDailyCount == resources.getInteger(R.integer.daily_activity_max_count)) {
                            val extraGold = resources.getInteger(R.integer.daily_counter_gold_reward)
                            activityAddedMessage = getString(R.string.third_activity_added_message, extraGold)
                            goldAmount += extraGold
                        }
                        Toast.makeText(context, activityAddedMessage, Toast.LENGTH_LONG).show()
                        activityDetailViewModel.updatePlayerValue(goldAmount, R.integer.default_starting_gold, R.string.saved_user_gold_key)
                    } else {
                        Toast.makeText(context, getString(R.string.activity_already_added_today_message), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        var isToolbarShown = false
        binding.activityDetailScrollview.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                val shouldShowToolbar = scrollY > binding.toolbar.height
                if (isToolbarShown != shouldShowToolbar) {
                    isToolbarShown = shouldShowToolbar
                    binding.appbar.isActivated = shouldShowToolbar
                    binding.toolbarLayout.isTitleEnabled = shouldShowToolbar
                }
            }
        )

        binding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        activityDetailViewModel.selectedActivity.observe(viewLifecycleOwner, {
            binding.activityPointsTitle.text = String.format(getString(R.string.activity_point_info), it.point)
            binding.activityGoldTitle.text = String.format(getString(R.string.activity_gold_info), it.gold)
        })

        activityDetailViewModel.lastAddedDate.observe(viewLifecycleOwner, {
            binding.activityHistoryDate.text = formatNullableDateString(it, getString(R.string.last_added_date_default))
        })

        return binding.root
    }

    interface Callback {
        fun add(activity: Activity?)
    }
}
