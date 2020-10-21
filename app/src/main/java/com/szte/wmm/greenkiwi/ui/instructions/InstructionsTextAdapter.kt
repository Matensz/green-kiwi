package com.szte.wmm.greenkiwi.ui.instructions

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.szte.wmm.greenkiwi.R

/**
 * Adapter for displaying the instructions texts in the instructions dialog's ViewPager2.
 */
class InstructionsTextAdapter(private val instructionsTexts: List<String>): RecyclerView.Adapter<TextItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.text_item_view, parent, false) as TextView
        return TextItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = instructionsTexts[position]
        holder.textView.text = item
    }

    override fun getItemCount(): Int {
        return instructionsTexts.size
    }
}

/**
 * Holds a single TextView item.
 */
class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)