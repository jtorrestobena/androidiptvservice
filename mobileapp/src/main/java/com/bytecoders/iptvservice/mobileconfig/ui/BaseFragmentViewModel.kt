package com.bytecoders.iptvservice.mobileconfig.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.m3u8parser.data.Playlist

abstract class BaseFragmentViewModel(protected val sharedViewModel: MainActivityViewModel): ViewModel() {

    val channelRepository get() = sharedViewModel.channelRepository
    val playlist: LiveData<Playlist> get () = sharedViewModel.playlist
}