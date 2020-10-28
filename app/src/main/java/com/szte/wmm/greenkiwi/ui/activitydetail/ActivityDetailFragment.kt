package com.szte.wmm.greenkiwi.ui.activitydetail

import android.content.Context
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

        val binding = DataBindingUtil.inflate<FragmentActivityDetailBinding>(
            inflater, R.layout.fragment_activity_detail, container, false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            activityViewModel = viewModel
            callback = object : Callback {

                override fun add(activity: Activity?) {
                    activity?.let {
                        viewModel.addActivity(activity.activityId)
                        updatePoints(activity.point)
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

    private fun updatePoints(points: Int) {
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
        val defaultValue = resources.getInteger(R.integer.default_starting_point).toLong()
        val currentPoints = sharedPref.getLong(getString(R.string.saved_user_points_key), defaultValue)
        with (sharedPref.edit()) {
            val updatedPoints = currentPoints + points.toLong()
            putLong(getString(R.string.saved_user_points_key), updatedPoints)
            apply()
        }
    }

    interface Callback {
        fun add(activity: Activity?)
    }
}