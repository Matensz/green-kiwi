package com.szte.wmm.greenkiwi.ui.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.ActivityHistoryListItemBinding
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivityWithDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Adapter for displaying the latest activities as the activity history list in a RecyclerView.
 */

class ActivityHistoryAdapter(private val context: Context) : ListAdapter<HistoryListItem, RecyclerView.ViewHolder>(DiffCallback) {

    private val headerType = 0
    private val dataType = 1

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ActivityHistoryViewHolder -> {
                val activity = getItem(position) as HistoryListItem.HistoryListData
                holder.bind(activity.userSelectedActivityWithDetails)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            headerType -> TextViewHolder.from(parent)
            dataType -> ActivityHistoryViewHolder.from(parent, context)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HistoryListItem.HistoryListHeader -> headerType
            is HistoryListItem.HistoryListData -> dataType
        }
    }

    fun addHeaderAndSubmitList(list: List<UserSelectedActivityWithDetails>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(HistoryListItem.HistoryListHeader)
                else -> listOf(HistoryListItem.HistoryListHeader) + list.map { HistoryListItem.HistoryListData(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<HistoryListItem>() {

        override fun areItemsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * ViewHolder for the header element in the RecyclerView.
     */
    class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.activity_history_header, parent, false)
                return TextViewHolder(view)
            }
        }
    }

    /**
     * Custom ViewHolder for the ActivityHistoryViewAdapter.
     */
    class ActivityHistoryViewHolder(private var binding: ActivityHistoryListItemBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup, context: Context): ActivityHistoryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ActivityHistoryListItemBinding.inflate(layoutInflater, parent, false)
                return ActivityHistoryViewHolder(binding, context)
            }
        }

        fun bind(activity: UserSelectedActivityWithDetails) {
            binding.activity = activity
            binding.point = String.format(context.getString(R.string.point_value), activity.point)
            binding.gold = String.format(context.getString(R.string.gold_value), activity.gold)
            binding.executePendingBindings()
        }
    }
}

/**
 * Wrapper class for the history list items for the RecyclerView.
 */
sealed class HistoryListItem {
    abstract val id: Long

    data class HistoryListData(val userSelectedActivityWithDetails: UserSelectedActivityWithDetails): HistoryListItem() {
        override val id = userSelectedActivityWithDetails.activityId
    }

    object HistoryListHeader: HistoryListItem() {
        override val id = Long.MIN_VALUE
    }
}