package com.szte.wmm.greenkiwi.ui.activitydetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentActivityDetailBinding

class ActivityDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val application = requireNotNull(activity).application

        //TODO handle potentional exception from requireArguments()
        val activity = ActivityDetailFragmentArgs.fromBundle(requireArguments()).selectedActivity
        val viewModelFactory = ActivityDetailViewModelFactory(activity, application)

        val binding = DataBindingUtil.inflate<FragmentActivityDetailBinding>(
            inflater, R.layout.fragment_activity_detail, container, false
        ).apply {
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

        binding.lifecycleOwner = this
        binding.activityViewModel = ViewModelProvider(
            this, viewModelFactory).get(ActivityDetailViewModel::class.java)

        return binding.root
    }
}