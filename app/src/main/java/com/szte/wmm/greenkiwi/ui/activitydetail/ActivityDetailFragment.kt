package com.szte.wmm.greenkiwi.ui.activitydetail

import android.content.Context
import android.content.SharedPreferences
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
import com.szte.wmm.greenkiwi.util.InjectorUtils
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentActivityDetailBinding
import com.szte.wmm.greenkiwi.repository.domain.Activity

class ActivityDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val application = requireNotNull(activity).application

        //TODO handle potentional exception from requireArguments()
        val activity = ActivityDetailFragmentArgs.fromBundle(requireArguments()).selectedActivity
        val viewModelFactory = InjectorUtils.getActivityDetailViewModelFactory(activity, this, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ActivityDetailViewModel::class.java)

        val sharedPref = application.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val binding = DataBindingUtil.inflate<FragmentActivityDetailBinding>(
            inflater, R.layout.fragment_activity_detail, container, false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            activityViewModel = viewModel
            callback = object : Callback {

                override fun add(activity: Activity?) {
                    activity?.let {
                        viewModel.addActivity(activity.activityId)
                        updatePlayerValue(sharedPref, activity.point, R.integer.default_starting_point, R.string.saved_user_points_key)
                        updatePlayerValue(sharedPref, activity.gold, R.integer.default_starting_gold, R.string.saved_user_gold_key)
                        Toast.makeText(application.applicationContext, R.string.activity_added_message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            var isToolbarShown = false
            activityDetailScrollview.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                    val shouldShowToolbar = scrollY > toolbar.height
                    if (isToolbarShown != shouldShowToolbar) {
                        isToolbarShown = shouldShowToolbar
                        appbar.isActivated = shouldShowToolbar
                        toolbarLayout.isTitleEnabled = shouldShowToolbar
                    }
                }
            )

            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }

        return binding.root
    }

    private fun updatePlayerValue(sharedPref: SharedPreferences, valueToAdd: Int, defaultResId: Int, sharedPrefKeyResId: Int) {
        val defaultValue = resources.getInteger(defaultResId).toLong()
        val currentValue = sharedPref.getLong(getString(sharedPrefKeyResId), defaultValue)
        with (sharedPref.edit()) {
            val updatedValue = currentValue + valueToAdd.toLong()
            putLong(getString(sharedPrefKeyResId), updatedValue)
            apply()
        }
    }

    interface Callback {
        fun add(activity: Activity?)
    }
}