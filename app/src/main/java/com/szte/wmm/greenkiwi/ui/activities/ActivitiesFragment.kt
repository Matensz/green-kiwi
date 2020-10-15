package com.szte.wmm.greenkiwi.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.szte.wmm.greenkiwi.InjectorUtils
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentActivitiesBinding

class ActivitiesFragment : Fragment() {

    private val activitiesViewModel: ActivitiesViewModel by viewModels {
        InjectorUtils.getActivitiesViewModelFactory(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentActivitiesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activities, container, false)
        binding.activitiesViewModel = activitiesViewModel
        binding.lifecycleOwner = this
        binding.activitiesList.adapter = ActivityAdapter(ActivityAdapter.OnClickListener {
            activitiesViewModel.displayActivityDetails(it)
        })

        activitiesViewModel.navigateToSelectedActivity.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(ActivitiesFragmentDirections.actionNavigationActivitiesToNavigationActivityDetail(it))
                activitiesViewModel.displayActivityDetailsComplete()
            }
        })

        activitiesViewModel.categories.observe(viewLifecycleOwner, object: Observer<List<String>> {
            override fun onChanged(data: List<String>?) {
                data ?: return
                val chipGroup = binding.categoriesList
                val inflator = LayoutInflater.from(chipGroup.context)
                val children = data.map { categoryName ->
                    val chip = inflator.inflate(R.layout.category, chipGroup, false) as Chip
                    chip.text = categoryName
                    chip.tag = categoryName
                    chip.setOnCheckedChangeListener { button, isChecked ->
                        activitiesViewModel.onFilterChanged(button.tag as String, isChecked)
                    }
                    chip
                }
                chipGroup.removeAllViews()

                for (chip in children) {
                    chipGroup.addView(chip)
                }
            }
        })

        return binding.root
    }
}