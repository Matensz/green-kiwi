package com.szte.wmm.greenkiwi.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.szte.wmm.greenkiwi.InjectorUtils
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentHomeBinding
import kotlin.math.truncate

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val currentPoints = getPoints()
        val viewModelFactory = InjectorUtils.getHomeViewModelFactory(currentPoints, resources.getInteger(R.integer.exp_base_number))
        val viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.homeViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.levelUps.observe(viewLifecycleOwner, Observer {
            binding.playerLevelText.text = String.format(resources.getString(R.string.level_info), it + 1)
        })

        viewModel.experience.observe(viewLifecycleOwner, Observer {
            binding.expText.text = String.format(resources.getString(R.string.exp_info), it.currentExp, it.currentMaxExp)
            binding.collectedExpBar.layoutParams.width = calculateExpBar(it.currentExp, it.currentMaxExp)
        })

        viewModel.petImage.observe(viewLifecycleOwner, Observer {
            binding.petImage.setImageResource(it)
        })

        binding.petImage.setOnClickListener {
            animatePet(it as ImageView)
        }

        return binding.root
    }

    private fun getPoints(): Long {
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return 0L
        val defaultValue = resources.getInteger(R.integer.default_starting_point).toLong()
        return sharedPref.getLong(getString(R.string.saved_user_points_key), defaultValue)
    }

    private fun calculateExpBar(currentPoints: Long, maxExpAtCurrentLevel: Long): Int {
        val lengthInPixels = currentPoints / maxExpAtCurrentLevel.toFloat() * resources.getInteger(R.integer.empty_exp_bar_length)
        val lengthInDp = truncate(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lengthInPixels, resources.displayMetrics)).toInt()
        return if (lengthInDp != 0) lengthInDp else 1
    }

    private fun animatePet(petImage: ImageView) {
        val animator = ObjectAnimator.ofFloat(petImage, View.ROTATION, 0f, 10f)
        animator.repeatCount = 5
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.duration = 300
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                petImage.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                petImage.isEnabled = true
            }
        })
        animator.start()
    }
}
