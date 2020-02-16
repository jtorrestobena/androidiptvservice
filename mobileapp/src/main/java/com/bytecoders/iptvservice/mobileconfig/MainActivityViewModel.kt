package com.bytecoders.iptvservice.mobileconfig

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservice.mobileconfig.repository.ChannelRepository
import com.bytecoders.iptvservicecommunicator.IPTVServiceClient
import com.bytecoders.m3u8parser.data.Playlist
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser

class MainActivityViewModel(application: Application): ViewModel() {
    internal val iptvClient: IPTVServiceClient by lazy {
        IPTVServiceClient(application)
    }

    val channelRepository = ChannelRepository()
    val playlist: LiveData<Playlist> = Transformations.map(channelRepository.playlist) { i -> i }
    val listings: LiveData<XmlTvParser.TvListing?> = Transformations.map(channelRepository.listing) { i -> i }
    val defaultPrefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(application)
    }

    fun savePositionOrder() {
        channelRepository.savePositionOrder()
    }
}

class MainActivityViewModelFactory (private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java))
            return MainActivityViewModel(application) as T
        throw IllegalArgumentException("Unexpected class $modelClass")
    }
}