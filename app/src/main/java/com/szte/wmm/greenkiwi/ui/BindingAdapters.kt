package com.szte.wmm.greenkiwi.ui

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.ui.activities.ActivityAdapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Activity>?) {
    val adapter = recyclerView.adapter as ActivityAdapter
    adapter.submitList(data) {
        recyclerView.scrollToPosition(0)
    }
}