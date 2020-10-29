package com.szte.wmm.greenkiwi.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
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
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentHomeBinding
import com.szte.wmm.greenkiwi.util.InjectorUtils
import kotlin.math.truncate

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application
        val currentPoints = getPoints()
        val levelCalculationBase = resources.getInteger(R.integer.exp_base_number)
        val viewModelFactory = InjectorUtils.getHomeViewModelFactory(currentPoints, levelCalculationBase, this, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.homeViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.levelUps.observe(viewLifecycleOwner, Observer {
            binding.playerLevelText.text = String.format(resources.getString(R.string.level_info), it + 1)
        })

        viewModel.experience.observe(viewLifecycleOwner, Observer {
            binding.expText.text = String.format(resources.getString(R.string.exp_info), it.currentValue, it.currentMaxValue)
            binding.collectedExpBar.layoutParams.width = calculateStatBar(it.currentValue, it.currentMaxValue)
        })

        viewModel.petImage.observe(viewLifecycleOwner, Observer {
            binding.petImage.setImageResource(it)
        })

        binding.petImage.setOnClickListener {
            animatePet(it as ImageView)
        }

        viewModel.dailyActivityCount.observe(viewLifecycleOwner, Observer {
            val maxCount = resources.getInteger(R.integer.daily_activity_max_count)
            binding.dailyActivityCounter.text = String.format(resources.getString(R.string.daily_activity_count_info), it, maxCount)
        })

        viewModel.hunger.observe(viewLifecycleOwner, Observer {
            val hungerPercent = truncate(it.currentValue / it.currentMaxValue.toFloat() * 100).toInt()
            binding.hungerText.text = String.format(resources.getString(R.string.hunger_info), hungerPercent, 100)
            binding.filledHungerBar.layoutParams.width = calculateStatBar(it.currentValue, it.currentMaxValue)
        })

        createNotificationChannel(
            getString(R.string.pet_hunger_channel_id),
            getString(R.string.pet_hunger_channel_name)
        )

        return binding.root
    }

    private fun getPoints(): Long {
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return 0L
        val defaultValue = resources.getInteger(R.integer.default_starting_point).toLong()
        return sharedPref.getLong(getString(R.string.saved_user_points_key), defaultValue)
    }

    private fun calculateStatBar(currentValue: Long, currentMaxValue: Long): Int {
        val lengthInPixels = currentValue / currentMaxValue.toFloat() * resources.getInteger(R.integer.empty_stat_bar_length)
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

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notifications of the kiwi's hunger"

            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
