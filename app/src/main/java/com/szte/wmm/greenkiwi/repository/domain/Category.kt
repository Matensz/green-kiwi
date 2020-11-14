package com.szte.wmm.greenkiwi.repository.domain

import com.szte.wmm.greenkiwi.R

/**
 * Enum representing the possible values of an activity's category.
 */
@Suppress("MagicNumber")
enum class Category(val id: Long, val stringResourceId: Int) {
    WATER_AND_ENERGY(1, R.string.water_and_energy),
    LESS_WASTE(2, R.string.less_waste),
    FOOD_AND_EATING_HABITS(3, R.string.food_and_eating_habits),
    TRANSPORTATION(4, R.string.transportation),
    BETTER_MATERIALS(5, R.string.better_materials),
    OTHER(0, R.string.other)
}
