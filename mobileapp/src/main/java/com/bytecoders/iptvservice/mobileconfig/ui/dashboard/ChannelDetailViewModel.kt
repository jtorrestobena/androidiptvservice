package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.ClassLayoutMapping
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.ViewHolderConfiguration
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.ViewHolderType
import com.bytecoders.iptvservice.mobileconfig.livedata.ChannelProgramMediator
import com.bytecoders.iptvservice.mobileconfig.livedata.SingleLiveEvent
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import com.bytecoders.m3u8parser.data.AlternativeURL
import com.bytecoders.m3u8parser.data.Track
import com.google.android.media.tv.companionlibrary.ProgramUtils
import com.google.android.media.tv.companionlibrary.model.Program

private const val TAG = "ChannelDetailViewModel"
class ChannelDetailViewModel(sharedViewModel: MainActivityViewModel) : BaseFragmentViewModel(sharedViewModel) {
    val track = MutableLiveData<Track>()
    val alternativeSelectedEvent = SingleLiveEvent<Int>()

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

    val layouts: ClassLayoutMapping = mapOf(Program::class to R.layout.epg_item)
    val viewConfig: ViewHolderConfiguration = ViewHolderConfiguration(ViewHolderType.EXPANDABLE, R.id.program_description)

    val layoutsAlternativeURL: ClassLayoutMapping = mapOf(AlternativeURL::class to R.layout.alternative_item)
    val viewConfigAlternativeURL: ViewHolderConfiguration = ViewHolderConfiguration()

    fun alternativeSelected(position: Int, alternative: Any) {
        Log.d(TAG, "Play alternative $alternative")
        alternativeSelectedEvent.postValue(position)
    }
}
