package com.bytecoders.iptvservice.mobileconfig.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.livedata.WifiConnectionLiveData
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListConfig
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListCustomConfig

class HomeViewModel(sharedViewModel: MainActivityViewModel)
    : BaseFragmentViewModel(sharedViewModel) {
    val m3uURL = sharedViewModel.channelRepository.m3uURL
    val epgURL = sharedViewModel.channelRepository.epgURL
    val newURLEvent = sharedViewModel.channelRepository.newURLEvent
    val serviceStatus = sharedViewModel.iptvClient.clientServiceLifecycle
    val wifiConnection = WifiConnectionLiveData(sharedViewModel.application)
    val tvName: LiveData<String?> = sharedViewModel.iptvClient.tvName

    val downloadProgress = channelRepository.percentage
    val channelsText = Transformations.map(channelsAvailable) {
        "$it channels"
    }

    val errorText = Transformations.map(playlist) {
        "${it.unknownEntries.size} unknown"
    }

    val channelEpg = Transformations.map(channelRepository.channelProgramCount) {
        "$it channel with EPG"
    }

    val programCount = Transformations.map(channelRepository.programCount) {
        "$it programs found"
    }

    fun downloadList() {
        m3uURL.value?.let (channelRepository::loadChannels)
        epgURL.value?.let (channelRepository::loadPlayListListings)
    }

    fun downloadNewEPG(newEPGUrl: String) = channelRepository.loadPlayListListings(newEPGUrl)

    fun sendList() {
        sharedViewModel.channelRepository.savedPositions.let {
            if (it.isNotEmpty()) {
                sharedViewModel.iptvClient.sendMessage(MessagePlayListCustomConfig(it))
            }
        }
        m3uURL.value?.let {
            sharedViewModel.iptvClient.sendMessage(MessagePlayListConfig(it, epgURL.value))
        }
    }
}