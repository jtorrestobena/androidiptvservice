package com.bytecoders.iptvservice.mobileconfig.ui.home

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservice.mobileconfig.livedata.StringSettings
import com.bytecoders.iptvservice.mobileconfig.repository.ChannelRepository
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListConfig
import com.bytecoders.m3u8parser.data.Playlist


private const val M3U_URL_PREFS = "M3U_URL_PREFS"
private const val EPG_URL_PREFS = "EPG_URL_PREFS"

class HomeViewModel(sharedPreferences: SharedPreferences) : BaseFragmentViewModel() {

    val channelRepository = ChannelRepository()
    val playlist: LiveData<Playlist> = Transformations.map(channelRepository.playlist) { i -> i }
    val m3uURL = StringSettings(sharedPreferences, M3U_URL_PREFS)
    val epgURL = StringSettings(sharedPreferences, EPG_URL_PREFS)

    fun downloadList() {
        m3uURL.value?.let (channelRepository::loadChannels)
    }

    fun sendList() {
        m3uURL.value?.let {
            sharedViewModel.iptvClient.sendMessage(MessagePlayListConfig(it, epgURL.value))
        }
    }
}

class HomeViewModelFactory(private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(sharedPreferences) as T
        throw IllegalArgumentException("Unexpected class $modelClass")
    }

}