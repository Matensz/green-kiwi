package com.szte.wmm.greenkiwi.repository.domain

/**
 * Enum representing the possible values of an activity's category.
 */
enum class Category(val id: Long) {
    WATER_AND_ENERGY(1),
    LESS_WASTE(2),
    FOOD(3),
    TRANSPORTATION(4),
    BETTER_MATERIALS(5),
    OTHER(0)
}