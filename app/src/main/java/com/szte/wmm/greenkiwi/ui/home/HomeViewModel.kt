package com.szte.wmm.greenkiwi.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.szte.wmm.greenkiwi.R

class HomeViewModel : ViewModel() {

    private val _expText = MutableLiveData<Int>()
    val expText: LiveData<Int>
        get() = _expText

    init {
        _expText.value = R.string.saved_user_points_key
    }
}