package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import androidx.lifecycle.MutableLiveData
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import com.bytecoders.m3u8parser.data.Track

class ChannelDetailViewModel(sharedViewModel: MainActivityViewModel) : BaseFragmentViewModel(sharedViewModel) {
    val track = MutableLiveData<Track>()
}
