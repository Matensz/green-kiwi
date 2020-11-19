package com.szte.wmm.greenkiwi.ui.shop

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.szte.wmm.greenkiwi.GreenKiwiApplication
import com.szte.wmm.greenkiwi.R
import com.szte.wmm.greenkiwi.databinding.FragmentShopBinding
import com.szte.wmm.greenkiwi.repository.domain.ShopCategory
import com.szte.wmm.greenkiwi.repository.domain.ShopItem
import kotlinx.coroutines.Dispatchers

/**
 * Fragment for the shop view.
 */
class ShopFragment : Fragment() {

    companion object {
        private const val DRAWABLE_FOLDER_NAME = "drawable"
        private const val STRINGS_FOLDER_NAME = "string"
        private const val DEFAULT_PET_IMAGE_NAME = "kiwi_green"
    }

    private lateinit var application: Application
    private lateinit var shopViewModel: ShopViewModel
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        application = requireActivity().application
        sharedPref = application.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val shopViewModelFactory = ShopViewModelFactory((application as GreenKiwiApplication).shopRepository, application, Dispatchers.IO)
        shopViewModel = ViewModelProvider(this, shopViewModelFactory).get(ShopViewModel::class.java)

        val binding: FragmentShopBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_shop, container, false)
        binding.shopViewModel = shopViewModel
        binding.lifecycleOwner = this
        binding.shopList.adapter = ShopItemGridAdapter(requireContext(), ShopItemGridAdapter.OnClickListener {
            createShopItemDialog(it).show()
        })
        binding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        shopViewModel.categories.observe(viewLifecycleOwner, object:
            Observer<List<ShopCategory>> {
            override fun onChanged(data: List<ShopCategory>?) {
                data ?: return
                val chipGroup = binding.shopCategoriesList
                val inflator = LayoutInflater.from(chipGroup.context)
                val children = data.map { category ->
                    val chip = inflator.inflate(R.layout.category, chipGroup, false) as Chip
                    chip.text = getString(category.stringResourceId)
                    chip.tag = category.name
                    chip.setOnCheckedChangeListener { button, isChecked ->
                        shopViewModel.onFilterChanged(button.tag as String, isChecked)
                    }
                    chip
                }
                chipGroup.removeAllViews()

                for (chip in children) {
                    chipGroup.addView(chip)
                }
            }
        })

        return binding.root
    }

    private fun createShopItemDialog(shopItem: ShopItem): AlertDialog {
        var builder = MaterialAlertDialogBuilder(requireActivity())
            .setNegativeButton(R.string.shop_item_dialog_negative_button_text) { dialog, _ ->
                dialog.cancel()
            }
        builder = if (!shopItem.purchased) buildBuyItemDialog(builder, shopItem) else buildUseItemDialog(builder, shopItem)
        return builder.create()
    }

    private fun buildBuyItemDialog(builder: MaterialAlertDialogBuilder, shopItem: ShopItem): MaterialAlertDialogBuilder {
        val currentPlayerGold = sharedPref.getLong(getString(R.string.saved_user_gold_key), 0L)
        val updatedBuilder = builder.setIcon(getResIdForImageName(shopItem.imageResourceName))
            .setTitle(getResIdForTitleName(shopItem.titleResourceName))
        if (currentPlayerGold < shopItem.price.toLong()) {
            updatedBuilder.setMessage(String.format(getString(R.string.shop_item_dialog_message_buy_not_enough_gold), shopItem.price, getCategoryName(shopItem.category)))
        } else {
            updatedBuilder.setMessage(String.format(getString(R.string.shop_item_dialog_message_buy), getCategoryName(shopItem.category), shopItem.price))
                .setPositiveButton(R.string.shop_item_dialog_positive_button_text_buy) { dialog, _ ->
                    shopViewModel.buySelectedItem(shopItem)
                    dialog.dismiss()
                }
        }
        return updatedBuilder
    }

    private fun buildUseItemDialog(builder: MaterialAlertDialogBuilder, shopItem: ShopItem): MaterialAlertDialogBuilder {
        val currentlyUsed = shopItem.imageResourceName == getCurrentlyUsedImageName(shopItem.category)
        val messageResource = if (currentlyUsed) R.string.shop_item_dialog_message_currently_used else R.string.shop_item_dialog_message_use
        val updatedBuilder = builder.setIcon(getResIdForImageName(shopItem.imageResourceName))
            .setTitle(getResIdForTitleName(shopItem.titleResourceName))
            .setMessage(String.format(getString(messageResource), getCategoryName(shopItem.category)))
        if (!currentlyUsed) {
            updatedBuilder.setPositiveButton(R.string.shop_item_dialog_positive_button_text_use) { dialog, _ ->
                shopViewModel.useSelectedItem(shopItem)
                dialog.dismiss()
            }
        }
        return updatedBuilder
    }

    private fun getResIdForImageName(resourceName: String): Int {
        return resources.getIdentifier(resourceName, DRAWABLE_FOLDER_NAME, requireContext().packageName)
    }

    private fun getResIdForTitleName(resourceName: String): Int {
        return resources.getIdentifier(resourceName, STRINGS_FOLDER_NAME, requireContext().packageName)
    }

    private fun getCategoryName(category: ShopCategory): String {
        return getString(when (category) {
            ShopCategory.BACKGROUND -> R.string.background
            ShopCategory.PET_IMAGE -> R.string.pet_image
        }).toLowerCase(resources.configuration.locale)
    }

    private fun getCurrentlyUsedImageName(category: ShopCategory): String {
        val preferenceKey = when (category) {
            ShopCategory.BACKGROUND -> R.string.current_background_key
            ShopCategory.PET_IMAGE -> R.string.current_pet_image_key
        }
        val defaultValue = when (category) {
            ShopCategory.BACKGROUND -> ""
            ShopCategory.PET_IMAGE -> DEFAULT_PET_IMAGE_NAME
        }
        return sharedPref.getString(getString(preferenceKey), defaultValue) ?: defaultValue
    }
}
