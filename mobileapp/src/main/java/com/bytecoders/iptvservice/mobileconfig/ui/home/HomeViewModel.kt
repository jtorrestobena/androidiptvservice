package com.bytecoders.iptvservice.mobileconfig.ui.home

import androidx.lifecycle.Transformations
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.livedata.StringSettings
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListConfig


private const val M3U_URL_PREFS = "M3U_URL_PREFS"
private const val EPG_URL_PREFS = "EPG_URL_PREFS"

class HomeViewModel(sharedViewModel: MainActivityViewModel)
    : BaseFragmentViewModel(sharedViewModel) {
    val m3uURL = StringSettings(sharedViewModel.defaultPrefs, M3U_URL_PREFS)
    val epgURL = StringSettings(sharedViewModel.defaultPrefs, EPG_URL_PREFS)
    val downloadProgress = channelRepository.percentage
    val channelsText = Transformations.map(playlist) {
        "${it.playListEntries.size} channels"
    }

    val errorText = Transformations.map(playlist) {
        "${it.unknownEntries.size} unknown"
    }

    fun downloadList() {
        m3uURL.value?.let (channelRepository::loadChannels)
        epgURL.value?.let (channelRepository::loadPlayList)
    }

    fun sendList() {
        m3uURL.value?.let {
            sharedViewModel.iptvClient.sendMessage(MessagePlayListConfig(it, epgURL.value))
        }
    }
}