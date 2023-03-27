package com.weather.featuretesting.presentation.mainscreen.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    val titleActionBar: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}
