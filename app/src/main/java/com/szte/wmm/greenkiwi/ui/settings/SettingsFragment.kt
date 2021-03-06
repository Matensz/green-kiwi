package com.szte.wmm.greenkiwi.ui.settings

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.szte.wmm.greenkiwi.GreenKiwiApplication
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentSettingsBinding
import com.szte.wmm.greenkiwi.ui.instructions.InstructionsDialogProvider
import kotlinx.coroutines.Dispatchers

/**
 * Fragment for the settings view.
 */
class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val application = requireNotNull(activity).application
        val sharedPref = application.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val viewModelFactory =
            SettingsViewModelFactory((application as GreenKiwiApplication).userSelectedActivitiesRepository, application.shopRepository, application, Dispatchers.IO)
        settingsViewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        binding.instructionsButton.setOnClickListener {
            InstructionsDialogProvider.createInstructionsDialog(requireActivity()).show()
        }

        val currentNightModeSetting = sharedPref.getBoolean(getString(R.string.night_mode_setting_key), false)
        binding.nightModeSwitch.isChecked = currentNightModeSetting
        binding.nightModeSwitch.setOnCheckedChangeListener{ _, isChecked ->
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
            settingsViewModel.saveNightModeSettings(isChecked)
        }

        binding.resetButton.setOnClickListener {
            createResetDialog(requireActivity()).show()
        }

        return binding.root
    }

    private fun createResetDialog(activity: Activity): AlertDialog {
        val builder = MaterialAlertDialogBuilder(activity)
        builder.setTitle(R.string.reset_dialog_title)
            .setMessage(R.string.reset_dialog_text)
            .setPositiveButton(R.string.reset_dialog_positive_button_text) { dialog, _ ->
                settingsViewModel.resetUserValues()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.reset_dialog_negative_button_text) { dialog, _ ->
                dialog.cancel()
            }
        return builder.create()
    }
}
