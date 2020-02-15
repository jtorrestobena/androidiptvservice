package com.bytecoders.iptvservice.mobileconfig.ui

import androidx.lifecycle.ViewModel
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel

abstract class BaseFragmentViewModel: ViewModel() {
    protected lateinit var sharedViewModel: MainActivityViewModel

    internal fun setActivityViewModel(activityViewModel: MainActivityViewModel) {
        sharedViewModel = activityViewModel
    }
}