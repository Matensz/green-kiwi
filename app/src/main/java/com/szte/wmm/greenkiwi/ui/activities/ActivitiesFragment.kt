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
import com.szte.wmm.greenkiwi.GreenKiwiApplication
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentActivitiesBinding
import com.szte.wmm.greenkiwi.repository.domain.Category
import kotlinx.coroutines.Dispatchers

/**
 * Fragment for the activities view.
 */
class ActivitiesFragment : Fragment() {

    private val activitiesViewModel: ActivitiesViewModel by viewModels {
        ActivitiesViewModelFactory((requireContext().applicationContext as GreenKiwiApplication).activitiesRepository, Dispatchers.IO)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentActivitiesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activities, container, false)
        binding.activitiesViewModel = activitiesViewModel
        binding.lifecycleOwner = this
        binding.activitiesList.adapter = ActivityAdapter(ActivityAdapter.OnClickListener {
            activitiesViewModel.displayActivityDetails(it)
        })

        activitiesViewModel.navigateToSelectedActivity.observe(viewLifecycleOwner, {
            if (it != null) {
                this.findNavController().navigate(ActivitiesFragmentDirections.actionNavigationActivitiesToNavigationActivityDetail(it))
                activitiesViewModel.displayActivityDetailsComplete()
            }
        })

        activitiesViewModel.categories.observe(viewLifecycleOwner, object: Observer<List<Category>> {
            override fun onChanged(data: List<Category>?) {
                data ?: return
                val chipGroup = binding.categoriesList
                val inflator = LayoutInflater.from(chipGroup.context)
                val children = data.map { category ->
                    val chip = inflator.inflate(R.layout.category, chipGroup, false) as Chip
                    chip.text = getString(category.stringResourceId)
                    chip.tag = category.name
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
