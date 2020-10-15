package com.szte.wmm.greenkiwi.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.szte.wmm.greenkiwi.R

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<Int>()
    val text: LiveData<Int>
        get() = _text

    init {
        _text.value = R.string.saved_user_points_key
    }
}