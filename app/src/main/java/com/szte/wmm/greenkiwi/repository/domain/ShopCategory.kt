package com.szte.wmm.greenkiwi.repository.domain

import com.szte.wmm.greenkiwi.R

/**
 * Enum representing the possible values of a shop item's category.
 */
enum class ShopCategory(val id: Int, val stringResourceId: Int) {
    BACKGROUND(1, R.string.background),
    PET_IMAGE(2, R.string.pet_image)
}
