package com.szte.wmm.greenkiwi.ui.shop

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.ShopGridItemBinding
import com.szte.wmm.greenkiwi.repository.domain.ShopCategory
import com.szte.wmm.greenkiwi.repository.domain.ShopItem
import com.szte.wmm.greenkiwi.util.getResIdForImageName
import com.szte.wmm.greenkiwi.util.getStringForResourceName

/**
 * Adapter for displaying items in the Shop View as a grid in a RecyclerView.
 */
class ShopItemGridAdapter(private val context: Context, private val onClickListener: OnClickListener) :
    androidx.recyclerview.widget.ListAdapter<ShopItem, ShopItemGridAdapter.ShopGridItemHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: ShopGridItemHolder, position: Int) {
        val shopItem = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(shopItem)
        }
        holder.bind(shopItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopGridItemHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ShopGridItemBinding.inflate(layoutInflater, parent, false)
        return ShopGridItemHolder(binding, context)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ShopItem>() {

        override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }
    }

    /**
     * Custom ViewHolder for the ShopItemGridAdapter.
     */
    class ShopGridItemHolder(private var binding: ShopGridItemBinding, private val context: Context) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(shopItem: ShopItem) {
            binding.shopItem = shopItem
            binding.shopItemImage.setImageResource(getImageResource(shopItem.imageResourceName))
            binding.shopItemImage.scaleType = getScaleType(shopItem.category)
            binding.shopItemName.text = getItemName(shopItem.titleResourceName)
            binding.purchasedStatus.setImageResource(getPurchasedStatusImage(shopItem.purchased))
            binding.executePendingBindings()
        }

        private fun getImageResource(resourceName: String): Int {
            return if (!resourceName.isBlank()) getResIdForImageName(context, resourceName) else R.color.primaryColor
        }

        private fun getItemName(resourceName: String): String {
            return getStringForResourceName(context, resourceName)
        }

        private fun getPurchasedStatusImage(purchased: Boolean): Int {
            return if (purchased) R.drawable.ic_baseline_check_circle_black_24dp else R.drawable.ic_baseline_shopping_cart_black_24dp
        }

        private fun getScaleType(category: ShopCategory): ImageView.ScaleType {
            return when(category) {
                ShopCategory.PET_IMAGE -> ImageView.ScaleType.CENTER_INSIDE
                ShopCategory.BACKGROUND -> ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    /**
     * Click listener for the grid items.
     */
    class OnClickListener(val clickListener: (shopItem: ShopItem) -> Unit) {
        fun onClick(shopItem: ShopItem) = clickListener(shopItem)
    }
}
