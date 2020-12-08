package com.szte.wmm.greenkiwi.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.ShopItem
import com.szte.wmm.greenkiwi.ui.activities.ActivityAdapter
import com.szte.wmm.greenkiwi.ui.shop.ShopItemGridAdapter

@BindingAdapter("listData")
fun bindRecyclerViewToList(recyclerView: RecyclerView, data: List<Activity>?) {
    val adapter = recyclerView.adapter as ActivityAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    val storageReference = Firebase.storage.reference.child(imgUrl?:"")

    imgUrl?.let {
        GlideApp.with(imgView.context)
            .load(storageReference)
            .into(imgView)
    }
}

@BindingAdapter("gridData")
fun bindRecyclerViewToGrid(recyclerView: RecyclerView, data: List<ShopItem>?) {
    val adapter = recyclerView.adapter as ShopItemGridAdapter
    adapter.submitList(data) {
        val lastPurchasedIndex = data?.indexOfFirst { it.lastPurchased }.let { if (it == -1) 0 else it } ?: 0
        recyclerView.scrollToPosition(lastPurchasedIndex)
    }
}
