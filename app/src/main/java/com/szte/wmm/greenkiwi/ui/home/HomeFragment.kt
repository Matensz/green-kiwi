package com.szte.wmm.greenkiwi.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.szte.wmm.greenkiwi.InjectorUtils
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels {
        InjectorUtils.getHomeViewModelFactory()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.homeViewModel = homeViewModel
        binding.lifecycleOwner = this

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.expText.text = getPoints(it)
        })

        return binding.root
    }

    private fun getPoints(keyId: Int): String {
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return ""
        val defaultValue = resources.getInteger(R.integer.default_starting_point).toLong()
        val currentPoints = sharedPref.getLong(getString(keyId), defaultValue)
        return currentPoints.toString()
    }
}