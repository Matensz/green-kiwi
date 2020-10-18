package com.szte.wmm.greenkiwi.ui.home

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
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
import kotlin.math.sqrt
import kotlin.math.truncate

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels {
        InjectorUtils.getHomeViewModelFactory()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.homeViewModel = homeViewModel
        binding.lifecycleOwner = this

        homeViewModel.expText.observe(viewLifecycleOwner, Observer {
            val currentPoints = getPoints(it)
            val levelUps = calculateLevelUpsInExpRange(currentPoints)
            val currentPlayerLevel = levelUps + 1
            val maxExpAtCurrentLevel = calculateMaxExpAtLevel(currentPlayerLevel)
            val maxExpAtPreviousLevel = calculateMaxExpAtLevel(levelUps)

            binding.playerLevelText.text = String.format(resources.getString(R.string.level_info), currentPlayerLevel)
            binding.expText.text = String.format(resources.getString(R.string.exp_info), currentPoints - maxExpAtPreviousLevel, maxExpAtCurrentLevel - maxExpAtPreviousLevel)
            binding.collectedExpBar.layoutParams.width = calculateExpBar(currentPoints - maxExpAtPreviousLevel, maxExpAtCurrentLevel - maxExpAtPreviousLevel)
        })

        return binding.root
    }

    private fun getPoints(keyId: Int): Long {
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return 0L
        val defaultValue = resources.getInteger(R.integer.default_starting_point).toLong()
        return sharedPref.getLong(getString(keyId), defaultValue)
    }

    private fun calculateLevelUpsInExpRange(currentExp: Long): Int {
        val expBase = resources.getInteger(R.integer.exp_base_number)
        val levelUpsInExpRange = (sqrt((expBase * expBase + 4 * expBase * currentExp).toDouble()) - expBase) / (2 * expBase)
        return truncate(levelUpsInExpRange).toInt()
    }

    private fun calculateMaxExpAtLevel(level: Int) =
        resources.getInteger(R.integer.exp_base_number).toLong() * level * (1 + level)

    private fun calculateExpBar(currentPoints: Long, maxExpAtCurrentLevel: Long) : Int {
        val lengthInPixels = currentPoints / maxExpAtCurrentLevel.toFloat() * resources.getInteger(R.integer.empty_exp_bar_length)
        val lengthInDp = truncate(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lengthInPixels, resources.displayMetrics)).toInt()
        return if (lengthInDp != 0) lengthInDp else 1
    }
}