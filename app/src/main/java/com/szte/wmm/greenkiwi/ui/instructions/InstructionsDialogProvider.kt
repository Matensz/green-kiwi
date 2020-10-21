package com.szte.wmm.greenkiwi.ui.instructions

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.szte.wmm.greenkiwi.R

/**
 * Provides an instance of the instructions dialog.
 */
object InstructionsDialogProvider {

    fun createInstructionsDialog(activity: Activity): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setIcon(R.drawable.kiwi)
            .setTitle(R.string.instructions_title)
            .setView(activity.layoutInflater.inflate(R.layout.fragment_instructions_dialog, null).let {
                val pager = it.findViewById<ViewPager2>(R.id.instructions_pager)
                val tabLayout = it.findViewById<TabLayout>(R.id.instructions_tab_layout)
                pager.adapter = InstructionsTextAdapter(getInstructionsTexts(activity.resources))
                TabLayoutMediator(tabLayout, pager) { tab, position ->

                }.attach()
                it
            })
            .setPositiveButton(R.string.instructions_dismiss) { dialog, _ ->
                updateReturningUserFlag(activity)
                dialog.dismiss()
            }
        return builder.create()
    }

    private fun getInstructionsTexts(resources: Resources): List<String> =
        listOf(resources.getString(R.string.instructions_first_page), resources.getString(R.string.instructions_second_page))

    private fun updateReturningUserFlag(activity: Activity) {
        val sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean(activity.getString(R.string.returning_user_key), true)
            apply()
        }
    }
}