package com.szte.wmm.greenkiwi.ui

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.ShopItem
import com.szte.wmm.greenkiwi.ui.activities.ActivityAdapter
import com.szte.wmm.greenkiwi.ui.shop.ShopItemGridAdapter

@BindingAdapter("listData")
fun bindRecyclerViewToList(recyclerView: RecyclerView, data: List<Activity>?) {
    val adapter = recyclerView.adapter as ActivityAdapter
    adapter.submitList(data) {
        recyclerView.scrollToPosition(0)
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)
    }
}

@BindingAdapter("gridData")
fun bindRecyclerViewToGrid(recyclerView: RecyclerView, data: List<ShopItem>?) {
    val adapter = recyclerView.adapter as ShopItemGridAdapter
    adapter.submitList(data) {
        recyclerView.scrollToPosition(0)
    }
}
