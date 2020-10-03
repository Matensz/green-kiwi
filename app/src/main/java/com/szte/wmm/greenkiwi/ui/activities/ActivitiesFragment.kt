package com.szte.wmm.greenkiwi.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.szte.wmm.greenkiwi.InjectorUtils
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentActivitiesBinding

class ActivitiesFragment : Fragment() {

    private val activitiesViewModel: ActivitiesViewModel by viewModels {
        InjectorUtils.getActivitiesViewModelFactory(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentActivitiesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_activities, container, false)
        binding.activitiesViewModel = activitiesViewModel
        binding.setLifecycleOwner(this)
        val adapter = ActivityAdapter()
        binding.activitiesList.adapter = adapter
        return binding.root
    }
}