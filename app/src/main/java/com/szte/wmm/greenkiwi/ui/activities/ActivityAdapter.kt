package com.szte.wmm.greenkiwi.ui.activities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.szte.wmm.greenkiwi.databinding.ActivityListItemBinding
import com.szte.wmm.greenkiwi.repository.domain.Activity

/**
 * Adapter for displaying the activities in as a list in a RecyclerView.
 */
class ActivityAdapter : ListAdapter<Activity, ActivityAdapter.ActivityViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ActivityListItemBinding.inflate(layoutInflater, parent, false)
        return ActivityViewHolder(binding)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Activity>() {

        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.activityId == newItem.activityId
        }
    }

    /**
     * Custom ViewHolder for the ActivityViewAdapter.
     */
    class ActivityViewHolder(private var binding: ActivityListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(activity: Activity) {
            binding.activity = activity
            binding.executePendingBindings()
        }
    }
}
