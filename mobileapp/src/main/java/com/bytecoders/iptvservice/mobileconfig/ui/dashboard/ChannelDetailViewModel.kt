package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.livedata.ChannelProgramMediator
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import com.bytecoders.m3u8parser.data.Track
import com.google.android.media.tv.companionlibrary.ProgramUtils
import com.google.android.media.tv.companionlibrary.model.Program


class ChannelDetailViewModel(sharedViewModel: MainActivityViewModel) : BaseFragmentViewModel(sharedViewModel) {
    val track = MutableLiveData<Track>()
    private val programChannelsMediator = ChannelProgramMediator().apply {
        addTrackSource(track)
        addListingSource(channelRepository.listing)
    }

    val programChannels : LiveData<List<Program>> by lazy {
        Transformations.map(programChannelsMediator) { i -> i }
    }

    val playingNow = MediatorLiveData<Program>().apply {
        addSource(programChannels) {
            postValue(ProgramUtils.getPlayingNow(it))
        }
    }
}
