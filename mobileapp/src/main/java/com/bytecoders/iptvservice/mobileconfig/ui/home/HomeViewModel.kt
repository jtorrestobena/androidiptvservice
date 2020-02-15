package com.bytecoders.iptvservice.mobileconfig.ui.home

import android.content.SharedPreferences
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.livedata.StringSettings
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import com.bytecoders.iptvservice.mobileconfig.ui.BaseViewModelFactory
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListConfig


private const val M3U_URL_PREFS = "M3U_URL_PREFS"
private const val EPG_URL_PREFS = "EPG_URL_PREFS"

class HomeViewModel(sharedPreferences: SharedPreferences, sharedViewModel: MainActivityViewModel)
    : BaseFragmentViewModel(sharedViewModel) {
    val m3uURL = StringSettings(sharedPreferences, M3U_URL_PREFS)
    val epgURL = StringSettings(sharedPreferences, EPG_URL_PREFS)
    val downloadProgress = channelRepository.percentage
    val channelsText = Transformations.map(playlist) {
        "${it.playListEntries.size} channels"
    }

    val errorText = Transformations.map(playlist) {
        "${it.unknownEntries.size} unknown"
    }

    fun downloadList() {
        m3uURL.value?.let (channelRepository::loadChannels)
    }

    fun sendList() {
        m3uURL.value?.let {
            sharedViewModel.iptvClient.sendMessage(MessagePlayListConfig(it, epgURL.value))
        }
    }
}

class HomeViewModelFactory(private val sharedPreferences: SharedPreferences,
                           sharedViewModel: MainActivityViewModel) : BaseViewModelFactory(sharedViewModel) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(sharedPreferences, sharedViewModel) as T
        throw IllegalArgumentException("Unexpected class $modelClass")
    }

}