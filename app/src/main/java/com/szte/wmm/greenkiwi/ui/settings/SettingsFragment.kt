package com.szte.wmm.greenkiwi.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.switchmaterial.SwitchMaterial
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.ui.instructions.InstructionsDialogProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val application = requireNotNull(activity).application
        val sharedPref = application.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val nightModeSettingKey = getString(R.string.night_mode_setting_key)

        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        val instructionsButton: Button = root.findViewById(R.id.instructions_button)
        instructionsButton.setOnClickListener {
            InstructionsDialogProvider.createInstructionsDialog(requireActivity()).show()
        }

        val nightModeSwitch: SwitchMaterial = root.findViewById(R.id.night_mode_switch)
        val nightMode = sharedPref.getBoolean(nightModeSettingKey, false)
        nightModeSwitch.isChecked = nightMode
        nightModeSwitch.setOnCheckedChangeListener{ _, isChecked ->
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
            lifecycleScope.launch {
                saveNightModeSetting(sharedPref, isChecked, nightModeSettingKey)
            }
        }

        return root
    }

    private suspend fun saveNightModeSetting(sharedPref: SharedPreferences, isChecked: Boolean, nightModeSettingKey: String) {
        withContext(Dispatchers.IO) {
            sharedPref.edit().putBoolean(nightModeSettingKey, isChecked).apply()
        }
    }
}