package com.bytecoders.iptvservice.mobileconfig.ui.home

import androidx.lifecycle.Transformations
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListConfig

class HomeViewModel(sharedViewModel: MainActivityViewModel)
    : BaseFragmentViewModel(sharedViewModel) {
    val m3uURL = sharedViewModel.channelRepository.m3uURL
    val epgURL = sharedViewModel.channelRepository.epgURL
    val newURLEvent = sharedViewModel.channelRepository.newURLEvent

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