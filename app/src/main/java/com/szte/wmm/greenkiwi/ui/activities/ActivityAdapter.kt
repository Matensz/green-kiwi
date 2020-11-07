package com.szte.wmm.greenkiwi.ui.activities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.szte.wmm.greenkiwi.databinding.ActivityListItemBinding
import com.szte.wmm.greenkiwi.repository.domain.Activity

/**
 * Adapter for displaying the activities as a list in a RecyclerView.
 */
class ActivityAdapter(private val onClickListener: OnClickListener) : ListAdapter<Activity, ActivityAdapter.ActivityViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(activity)
        }
        holder.bind(activity)
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

    /**
     * Click listener for the list items.
     */
    class OnClickListener(val clickListener: (activity: Activity) -> Unit) {
        fun onClick(activity: Activity) = clickListener(activity)
    }
}
