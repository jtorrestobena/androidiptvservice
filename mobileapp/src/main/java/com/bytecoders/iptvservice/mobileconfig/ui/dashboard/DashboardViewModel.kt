package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel

class DashboardViewModel : BaseFragmentViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
}