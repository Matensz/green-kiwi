package com.szte.wmm.greenkiwi

import com.szte.wmm.greenkiwi.repository.domain.Activity
import com.szte.wmm.greenkiwi.repository.domain.Category
import com.szte.wmm.greenkiwi.repository.domain.ShopCategory
import com.szte.wmm.greenkiwi.repository.domain.ShopItem
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivity
import com.szte.wmm.greenkiwi.repository.domain.UserSelectedActivityWithDetails

/**
 * Default test time, which represents 2020-11-15 19:00:00 in millis
 */
const val CURRENT_TIME = 1605463200000L

/**
 * Creates a default Activity.
 * @param id the id of the activity
 * @param category the category of the activity represented by Category
 * @return an Activity
 */
fun createActivity(id: Long, category: Category): Activity {
    return Activity(id, "title", "description", "thumbnailUrl", "imageUrl", 1, 1, category)
}

/**
 * Creates a default ShopItem.
 * @param id the id of the shop item
 * @param category the category of the item represented by ShopCategory
 * @param purchased the purchased status of the item
 * @return a ShopItem
 */
fun createShopItem(id: Long, category: ShopCategory, purchased: Boolean): ShopItem {
    return ShopItem(id, "titleName", "imageName", 1, category, purchased)
}

/**
 * Creates a default UserSelectedActivity.
 * @param id the id of the activity
 * @param timeAdded the time the activity was added
 * @return a UserSelectedActivity
 */
fun createUserSelectedActivity(id: Long, timeAdded: Long): UserSelectedActivity {
    return UserSelectedActivity(id = id, activityId = id, timeAdded = timeAdded)
}

/**
 * Creates a default UserSelectedActivityWithDetails.
 * @param id the id of the activity
 * @return a UserSelectedActivityWithDetails
 */
fun createUserSelectedActivityWithDetails(id: Long): UserSelectedActivityWithDetails {
    return UserSelectedActivityWithDetails(id, "title", 1, 1, Category.WATER_AND_ENERGY, "2020.11.15")
}
