package com.szte.wmm.greenkiwi.util

import android.content.Context
import android.content.res.Resources

/**
 * Returns the full resource id of a drawable for the given name.
 */
fun getResIdForImageName(context: Context, resourceName: String): Int {
    return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
}

/**
 * Finds and returns a string resource value for the given string resource name.
 */
fun getStringForResourceName(context: Context, resourceName: String): String {
    val resources: Resources = context.resources
    val resId = resources.getIdentifier(resourceName, "string", context.packageName)
    return resources.getString(resId)
}
