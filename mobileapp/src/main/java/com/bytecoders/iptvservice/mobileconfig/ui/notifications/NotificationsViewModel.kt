package com.bytecoders.iptvservice.mobileconfig.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel

class NotificationsViewModel(sharedViewModel: MainActivityViewModel)
    : BaseFragmentViewModel(sharedViewModel) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}