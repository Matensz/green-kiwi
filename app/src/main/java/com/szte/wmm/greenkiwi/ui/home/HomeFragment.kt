package com.szte.wmm.greenkiwi.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentHomeBinding
import com.szte.wmm.greenkiwi.ui.home.context.HomeDataContext
import com.szte.wmm.greenkiwi.util.InjectorUtils
import java.text.DecimalFormat
import kotlin.math.truncate

class HomeFragment : Fragment() {

    companion object {
        private const val DEFAULT_NICKNAME = "Pea"
    }

    private lateinit var application: Application
    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        application = requireNotNull(activity).application
        sharedPref = application.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val currentPoints = getPoints()
        val levelCalculationBase = resources.getInteger(R.integer.exp_base_number)
        val context = HomeDataContext(currentPoints, levelCalculationBase)
        val viewModelFactory = InjectorUtils.getHomeViewModelFactory(context, this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.homeViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.levelUps.observe(viewLifecycleOwner, {
            val currentLevel = it + 1
            binding.playerLevelText.text = String.format(getString(R.string.level_info), currentLevel)
            binding.petNicknameText.visibility = if (currentLevel > 4) View.VISIBLE else View.INVISIBLE
        })

        viewModel.experience.observe(viewLifecycleOwner, {
            binding.expText.text = String.format(getString(R.string.exp_info), it.currentValue, it.currentMaxValue)
            binding.collectedExpBar.layoutParams.width = calculateStatBar(it.currentValue, it.currentMaxValue)
        })

        viewModel.petImage.observe(viewLifecycleOwner, {
            binding.petImage.setImageResource(it)
        })

        viewModel.currentBackground.observe(viewLifecycleOwner, {
            binding.homeParentLayout.setBackgroundResource(it ?: R.color.secondaryColor)
        })

        viewModel.hunger.observe(viewLifecycleOwner, {
            val hungerPercent = truncate(it.currentValue / it.currentMaxValue.toFloat() * 100).toInt()
            binding.hungerText.text = String.format(getString(R.string.hunger_info), hungerPercent, 100)
            binding.filledHungerBar.layoutParams.width = calculateStatBar(it.currentValue, it.currentMaxValue)
        })

        viewModel.gold.observe(viewLifecycleOwner, {
            val formatterString = getString(R.string.gold_formatter)
            val formattedGold = DecimalFormat(formatterString).format(it)
            binding.playerGoldText.text = String.format(getString(R.string.gold_info), formattedGold)
        })

        viewModel.feedButtonVisible.observe(viewLifecycleOwner, {
            binding.feedPetButton.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.navigateToShop.observe(viewLifecycleOwner, {
            if (it != null) {
                this.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToShopFragment())
                viewModel.navigateToShopComplete()
            }
        })

        binding.petImage.setOnClickListener {
            animatePet(it as ImageView)
        }

        binding.petNicknameText.text = formatNickname()
        binding.petNicknameText.setOnClickListener {
            editPetNickname()
        }

        binding.petNicknameButton.setOnClickListener {
            confirmPetNickname()
        }

        binding.feedPetButton.setOnClickListener {
            createPetFeedDialog().show()
        }

        binding.playerGoldText.setOnClickListener {
            viewModel.navigateToShop()
        }

        createNotificationChannel(
            getString(R.string.pet_hunger_channel_id),
            getString(R.string.pet_hunger_channel_name)
        )

        return binding.root
    }

    private fun getPoints(): Long {
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

    private fun formatNickname(): String {
        val petNickname = sharedPref.getString(getString(R.string.pet_nickname_key), "")
        return if (petNickname.isNullOrBlank()) getString(R.string.pet_nickname_hint) else String.format(getString(R.string.pet_nickname_text), petNickname)
    }

    private fun editPetNickname() {
        binding.apply {
            petNicknameText.visibility = View.GONE
            petNicknameEdit.visibility = View.VISIBLE
            petNicknameButton.visibility = View.VISIBLE
        }
        binding.petNicknameEdit.requestFocus()
        val inputMethodManager = application.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.petNicknameEdit, 0)
    }

    private fun confirmPetNickname() {
        binding.apply {
            val newPetNickname = if (!petNicknameEdit.text.toString().isBlank()) petNicknameEdit.text.toString() else DEFAULT_NICKNAME
            petNicknameText.text = String.format(getString(R.string.pet_nickname_text), newPetNickname)
            invalidateAll()
            petNicknameText.visibility = View.VISIBLE
            petNicknameEdit.visibility = View.GONE
            petNicknameButton.visibility = View.GONE
            sharedPref.edit().putString(getString(R.string.pet_nickname_key), newPetNickname).apply()
        }
        val inputMethodManager = application.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.petNicknameButton.windowToken, 0)
    }

    private fun createPetFeedDialog(): AlertDialog {
        val dialogText = getString(R.string.feed_pet_dialog_text)
        val foodPrice = resources.getInteger(R.integer.food_price_in_gold)
        val defaultNickname = getString(R.string.feed_pet_dialog_text_default_nickname)
        val nickname = sharedPref.getString(getString(R.string.pet_nickname_key), defaultNickname)
        val playerGold = sharedPref.getLong(getString(R.string.saved_user_gold_key), 0L)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.feed_pet_dialog_title)
            .setMessage(String.format(dialogText, foodPrice, nickname))
            .setNegativeButton(R.string.feed_pet_dialog_negative_button_text) { dialog, _ -> dialog.cancel() }
        if (playerGold >= foodPrice.toLong()) {
            builder.setPositiveButton(R.string.feed_pet_dialog_positive_button_text) { dialog, _ ->
                viewModel.feedPet()
                dialog.dismiss()
            }
        } else {
            Toast.makeText(context, R.string.feed_pet_toast_message, Toast.LENGTH_SHORT).show()
        }
        return builder.create()
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notifications of the kiwi's hunger"

            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
