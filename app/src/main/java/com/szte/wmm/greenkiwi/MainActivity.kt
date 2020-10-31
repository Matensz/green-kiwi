package com.szte.wmm.greenkiwi

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.szte.wmm.greenkiwi.ui.instructions.InstructionsDialogProvider

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initNightModeSettings()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        if (!isReturningUser()) {
            InstructionsDialogProvider.createInstructionsDialog(this).show()
        }
    }

    private fun isReturningUser(): Boolean {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return true
        return sharedPref.getBoolean(getString(R.string.returning_user_key), false)
    }

    private fun initNightModeSettings() {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
        if (sharedPref.getBoolean(getString(R.string.night_mode_setting_key), false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}