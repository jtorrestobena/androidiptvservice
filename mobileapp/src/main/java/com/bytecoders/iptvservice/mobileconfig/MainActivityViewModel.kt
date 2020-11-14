package com.bytecoders.iptvservice.mobileconfig

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservice.mobileconfig.livedata.SingleLiveEvent
import com.bytecoders.iptvservice.mobileconfig.repository.ChannelRepository
import com.bytecoders.iptvservicecommunicator.IPTVServiceClient
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.data.Track
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser

class MainActivityViewModel(val application: Application): ViewModel() {
    val iptvClient: IPTVServiceClient by lazy {
        IPTVServiceClient(application)
    }

    val defaultPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    val channelRepository by lazy { ChannelRepository(application) }
    val playlist: LiveData<Playlist> = Transformations.map(channelRepository.playlist) { i -> i }
    val listings: LiveData<XmlTvParser.TvListing?> = Transformations.map(channelRepository.listing) { i -> i }
    val newPlaylistEvent = SingleLiveEvent<String>()

    fun savePositionOrder() {
        channelRepository.savePositionOrder()
    }

    fun validateURL(urlToValidate: String) {
        if (channelRepository.isPlayListURL(urlToValidate)) {
            newPlaylistEvent.value = urlToValidate
        }
    }

    fun getChannelWithId(id: String): Track? = playlist.value?.playListEntries?.firstOrNull {
            it.identifier == id
        }

    fun getChannelPosition(track: Track): Int = playlist.value?.playListEntries?.indexOf(track) ?: -1
}

class MainActivityViewModelFactory (private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java))
            return MainActivityViewModel(application) as T
        throw IllegalArgumentException("Unexpected class $modelClass")
    }
}